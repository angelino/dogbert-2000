(ns dogbert-2000.core
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [compojure.route :refer [not-found]]
   [hiccup.core :refer [html]]
   [hiccup.form :refer [form-to]]
   [hiccup.page :refer [html5]]
   [ring.handler.dump :refer [handle-dump]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.util.response :refer [redirect response]]
   [taoensso.carmine :as carmine]
   [taoensso.telemere :as t]))

(defn header-component []
  (html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.2/css/bulma.css"}]]))

(defn shortener-form []
  (html (form-to [:post "/urls"]
                 [:div.field.has-addons
                  [:div.control
                   [:input.input
                    {:type :text
                     :name "url"
                     :placeholder "Shorten an url"}]]
                  [:div.control
                   [:button.button.is-info {:type :submit} "Shorten"]]])))

(defn index-page [pages]
  (html5
   (header-component)
   [:body.container
    [:div.columns
     [:div.column
      [:h1.title.is-1 "Dogbert 2000 (A Bit.ly clone)"]]]
    [:div.columns
     [:div.column (shortener-form)]]
    [:div.columns
     [:div.column
      [:ul
       (for [page pages]
         [:li
          [:a {:href (:shorten-url page)} (:url-id page)]
          " - "
          (:url page)])]]]]))

#_(defonce database (atom {}))

#_(defn store-url! [{:keys [shorten-url url]}]
    (swap! database assoc shorten-url url))

#_(defn get-url [url-id]
    (get @database url-id))

#_(defn get-urls []
    (map #(hash-map :url-id (first %)
                    :shorten-url (first %)
                    :url (last %))
         @database))

(defonce conn-opts {:spec {:uri (or (System/getenv "REDIS_URI")
                                    "redis://localhost:6379/1")}
                    #_#_:pool (redis/connection-pool {})})
(comment
  (carmine/wcar
   conn-opts
   (carmine/ping)
   (redis/set "Hello" "World!")
   (carmine/get "Hello"))
  :end)

(defn store-url! [{:keys [shorten-url url]}]
  (t/log! :info (str "Storing " url " as " shorten-url))
  (carmine/wcar
   conn-opts
   (carmine/set shorten-url url)))

(defn get-url [url-id]
  (carmine/wcar
   conn-opts
   (carmine/get url-id)))

(defn get-urls []
  (map #(hash-map :url-id %
                  :shorten-url %
                  :url (get-url %))
       (carmine/wcar conn-opts (carmine/keys "*"))))

(defn url-for [{:keys [scheme server-name server-port]} resource]
  (str (name scheme) "://" server-name ":" server-port "/" resource))

(defn handle-index-url [req]
  (let [urls (map #(update % :shorten-url (partial url-for req))
                  (get-urls))]
    (t/log! :info (into [] urls)) ;; FIXME: really need it?
    (response (index-page urls))))

(defn handle-redirect-url [req]
  (let [url-id (get-in req [:params :url])
        target-url (get-url url-id)]
    (t/log! :info (str "Redirecting " url-id " to " target-url))
    (redirect target-url)))

(defn shorten-url [url]
  (str (gensym "dog")))

(defn handle-create-url [req]
  (let [url (get-in req [:params "url"])]
    (store-url! {:shorten-url (shorten-url url) :url url})
    (redirect "/urls")))

(defroutes routes
  (GET "/debug" [] handle-dump)
  (GET  "/"     [] handle-index-url)
  (GET  "/urls" [] handle-index-url)
  (POST "/urls" [] handle-create-url)
  (GET  "/:url" [] handle-redirect-url)
  (not-found "URL not found!!!"))

(def app
  (-> routes
      wrap-params))

