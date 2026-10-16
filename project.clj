(defproject clojure-amqp-example "1.0.0-SNAPSHOT"
  :description "Example of publishing and consuming messages with Langohr"
  :main clojure-amqp-example.core
  :dependencies [[org.clojure/clojure "1.12.6"]
                 [com.novemberain/langohr "5.6.0"]
                 ;; amqp-client depends on slf4j-api 1.7, which slf4j-simple 2 can't bind to
                 [org.slf4j/slf4j-api "2.0.20"]
                 [org.slf4j/slf4j-simple "2.0.20"]]
  :profiles {:uberjar {:aot :all}})
