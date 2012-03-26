(ns clojure-amqp-example.test.core
  (:use [clojure-amqp-example.core])
  (:use [clojure.test]))

(deftest can-split-url
         (let [config (split-amqp-url "amqp://foo:bar@example.com/vhost")
               opts {:vhost "vhost" :host "example.com" 
                     :password "bar" :username "foo"}]
           (is (= opts config))))

(deftest empty-map-for-nil-string
         (let [config (split-amqp-url nil)
               opts {}]
           (is (= opts config))))
