(ns dogbert-2000.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [response]]
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

(defn handle-hello [req]
  (response (index-page)))

(defn -main [port]
  (jetty/run-jetty handle-hello {:port (Integer. port)}))
