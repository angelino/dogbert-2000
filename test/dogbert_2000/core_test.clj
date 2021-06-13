(ns dogbert-2000.core-test
  (:require [clojure.test :refer :all]
            [dogbert-2000.core :refer :all]))

(deftest handle-index-url-test
  (testing "GET '/' must show the index page with status 200."
    (let [response (handle-index-url {})]
      #_(prn response)
      (is (= 200 (:status response)))))

  (testing "GET '/urls' must show de index page with status 200."
    (let [response (handle-index-url {})]
      #_(prn response)
      (is (= 200 (:status response))))))
