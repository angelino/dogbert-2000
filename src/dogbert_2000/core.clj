(ns dogbert-2000.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [response]]))

; {:status 200
;  :headers {}
;  :body "Hello"} 
(defn handle-hello [req]
  (response "Hello"))

(defn -main [port]
  (jetty/run-jetty handle-hello {:port (Integer. port)}))
