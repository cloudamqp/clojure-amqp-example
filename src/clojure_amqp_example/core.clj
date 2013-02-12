(ns clojure-amqp-example.core
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))

(def amqp-conn 
  (let [uri (get (System/getenv) "CLOUDAMQP_URL")]
    (rmq/connect {:uri uri}))) ;; Connect to the broker

(defn publish-periodically []
  (.start (Thread. 
            #(let [channel (lch/open amqp-conn)
                   exchange ""
                   queue    "cloudamqp.example"]
               (lq/declare channel queue :auto-delete true :exclusive false)
               (println "Publishing to" queue)
               (loop [i 1]
                 (lb/publish channel exchange queue (str "Message " i))
                 (Thread/sleep 1000)
                 (recur (inc i)))))))

(defn print-messages []
  (let [channel (lch/open amqp-conn)
        exchange ""
        queue    "cloudamqp.example"]
    (lq/declare channel queue :auto-delete true :exclusive false)
    (lc/subscribe channel queue (fn [ch metadata payload]
                                  (println (String. payload)))
                  :auto-ack true)
    (println "Subscribing to" queue)))

(defn -main [& args]
  (publish-periodically)
  (print-messages))
