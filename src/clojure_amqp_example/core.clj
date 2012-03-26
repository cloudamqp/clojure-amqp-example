(ns clojure-amqp-example.core
  (:require [langohr.core      :as lhc]
            [langohr.basic     :as lhb]
            [langohr.queue     :as lhq]
            ))

(defn split-amqp-url [url]
  "Parses AMQP urls, eg. amqp@user:pass@lemur.cloudamqp.com/vhost"
  (if (not (nil? url))
    (let [matcher (re-matcher #"^.*://(.*?):(.*?)@(.*?)/(.*)$" url)]
      (when (.find matcher) ;; Check if it matches.
        (dissoc 
          (zipmap [:match :username :password :host :vhost] (re-groups matcher))
          :match)))
    {}))

(defn -main [& args]
  (let [url (get (System/getenv) "CLOUDAMQP_URL") ;; Get the URL
        config (split-amqp-url url) ;; Split to URL to a map
        conn (lhc/connect config) ;; open the connection to CloudAMQP
        channel (.createChannel conn) ;; open a channel
        exchange "" ;; use the default excahnge
        queue (.getQueue (lhq/declare channel exchange :auto-delete true))
        payload "Hello CloudAMQP!"]
    (lhb/publish channel exchange queue payload) ;; Publish the payload
    (println (String. (.getBody (lhb/get channel queue)))) ;; Get and print
    (.close conn) ;; close the connection
    ))
