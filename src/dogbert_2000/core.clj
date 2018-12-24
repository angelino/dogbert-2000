(ns dogbert-2000.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [response redirect]]
            [ring.handler.dump :refer [handle-dump]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes GET POST]]
            [hiccup.page :refer [html5]]
            [hiccup.core :refer [html]]
            [hiccup.form :refer [form-to
                                 text-field
                                 submit-button]]))

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

(defn index-page []
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.2/css/bulma.css"}]]
   [:body
    [:div.container
     [:h1.title.is-1 "Dogbert 2000 (A Bit.ly clone)"]
     [:div (shortener-form)]]]))

(defn handle-index [req]
  (response (index-page)))

(defn handle-show-url [req]
  (redirect (str "http://www.google.com?q=" (get-in req [:params :url-id]))))

(defroutes routes
  (GET "/" [] handle-index)
  (GET "/:url-id" [] handle-show-url)
  (POST "/urls" [] handle-dump))

(def app
  (-> routes
      wrap-params))

(defn -main [port]
  (jetty/run-jetty app {:port (Integer. port)}))
