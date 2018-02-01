(ns dogbert-2000.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [response]]
            [hiccup.page :refer [html5]]))

(defn index-page []
  (html5
   [:head]
   [:body
    [:h1 "Dogbert 2000 (A Bit.ly clone)"]]))

; {:status 200
;  :headers {}
;  :body "Hello"} 
(defn handle-hello [req]
  (response (index-page)))

(defn -main [port]
  (jetty/run-jetty handle-hello {:port (Integer. port)}))
