(ns clojure-amqp-example.core-test
  (:require [clojure.test :refer [deftest is]]
            [clojure-amqp-example.core :as example]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq])
  (:import [javax.net.ssl SSLContext]))

(deftest amqps-verifies-the-server-certificate
  (let [settings (example/connection-settings "amqps://user:pass@host/vhost")]
    (is (= (SSLContext/getDefault) (:ssl-context settings)))
    (is (true? (:verify-hostname settings)))))

(deftest amqp-does-not-use-tls
  (let [settings (example/connection-settings "amqp://guest:guest@localhost")]
    (is (nil? (:ssl-context settings)))))

(deftest worker-receives-published-message
  (let [queue    (str "roundtrip-" (random-uuid))
        received (promise)
        conn     (example/connect)]
    (try
      (example/consume (lch/open conn) queue #(deliver received %))
      (example/publish (lch/open conn) queue "Hello CloudAMQP!")
      (is (= "Hello CloudAMQP!" (deref received 5000 :timeout)))
      (finally
        (lq/delete (lch/open conn) queue)
        (rmq/close conn)))))
