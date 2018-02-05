(ns dogbert-2000.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [response]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET POST]]
            [hiccup.page :refer [html5]]
            [hiccup.core :refer [html]]
            [hiccup.form :refer [form-to
                                 text-field
                                 submit-button]]))

(defn shortener-form []
  (html (form-to [:post "/urls"]
                 (text-field "url")
                 (submit-button "Shorten"))))

(defn index-page []
  (html5
   [:head]
   [:body
    [:h1 "Dogbert 2000 (A Bit.ly clone)"]
    [:div.content
     (shortener-form)]]))

(defn handle-index [req]
  (response (index-page)))

(defroutes routes
  (GET "/" [] handle-index)
  (POST "/urls" [] handle-dump))

(def app
  routes)

(defn -main [port]
  (jetty/run-jetty app {:port (Integer. port)}))
