(ns clojure-amqp-example.core
  (:require [clojure.string :as str]
            [langohr.basic :as lb]
            [langohr.channel :as lch]
            [langohr.confirm :as lcf]
            [langohr.consumers :as lc]
            [langohr.core :as rmq]
            [langohr.queue :as lq])
  (:import [javax.net.ssl SSLContext])
  (:gen-class))

(defn connection-settings
  "Settings for CLOUDAMQP_URL. For amqps:// the JVM's default SSLContext
  verifies the server certificate and hostname; without it Langohr's
  client would trust any certificate."
  [uri]
  (cond-> {:uri uri}
    (str/starts-with? uri "amqps://") (assoc :ssl-context (SSLContext/getDefault)
                                             :verify-hostname true)))

(defn connect []
  (rmq/connect (connection-settings (or (System/getenv "CLOUDAMQP_URL")
                                        "amqp://guest:guest@localhost"))))

(defn publish
  "Publishes a persistent message and waits for the broker's confirm"
  [ch queue message]
  (lq/declare ch queue {:durable true :exclusive false :auto-delete false})
  (lcf/select ch)
  (lb/publish ch "" queue message {:content-type "text/plain" :persistent true})
  (lcf/wait-for-confirms-or-die ch 5000)
  (println (format " [x] Sent '%s'" message)))

(defn consume
  "Consumes with a prefetch of 10, acking each message after handler returns"
  [ch queue handler]
  (lq/declare ch queue {:durable true :exclusive false :auto-delete false})
  (lb/qos ch 10)
  (lc/subscribe ch queue
                (fn [ch {:keys [delivery-tag]} ^bytes payload]
                  (handler (String. payload "UTF-8"))
                  (lb/ack ch delivery-tag))
                {:auto-ack false}))

(defn -main [& [mode]]
  (let [conn (connect)]
    (case mode
      "publish" (do (publish (lch/open conn) "hello" "Hello CloudAMQP!")
                    (rmq/close conn))
      "worker"  (do (consume (lch/open conn) "hello" #(println (format " [x] Received '%s'" %)))
                    (println " [*] Waiting for messages. To exit press CTRL+C"))
      (do (println "Usage: lein run publish|worker")
          (rmq/close conn)
          (System/exit 1)))))
