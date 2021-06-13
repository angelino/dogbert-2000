(ns dogbert-2000.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [response redirect]]
            [ring.handler.dump :refer [handle-dump]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [not-found]]
            [hiccup.page :refer [html5]]
            [hiccup.core :refer [html]]
            [hiccup.form :refer [form-to
                                 text-field
                                 submit-button]]))

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

(defonce database (atom {}))

(defn url-for [{:keys [scheme server-name server-port]} resource]
  (str (name scheme) "://" server-name ":" server-port "/" resource))

(defn handle-index-url [req]
  (let [urls (map #(hash-map :url-id (first %)
                             :shorten-url (url-for req (first %))
                             :url (last %))
                  @database)]
    (response (index-page urls))))

(defn handle-redirect-url [req]
  (let [url-id (get-in req [:params :url])
        target-url (get @database url-id)]
    (println "Redirecting" url-id "to" target-url)
    (redirect target-url)))

(defn shorten-url [url]
  (str (gensym "dog")))

(defn handle-create-url [req]
  (let [url (get-in req [:params "url"])]
    (swap! database assoc (shorten-url url) url)
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

(defn -main [& [port]]
  (let [p (Integer. (or port 5000))]
    (println "Server running on port:" p)
    (jetty/run-jetty app {:port p})))
