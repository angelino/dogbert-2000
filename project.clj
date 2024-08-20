(defproject dogbert-2000 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.4"]
                 [com.taoensso/telemere "1.0.0-beta16"]
                 [com.taoensso/slf4j-telemere "1.0.0-beta16"]
                 [com.taoensso/carmine "3.4.1"]
                 [ring "1.12.2"]
                 [compojure "1.7.1"]
                 [hiccup "1.0.5"]]
  :plugins [[lein-ring "0.12.6"]]
  :ring {:handler dogbert-2000.core/app}
  :main dogbert-2000.core)
