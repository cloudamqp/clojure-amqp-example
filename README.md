# Use CloudAMQP from Clojure

A minimal example of publishing and consuming messages with
[Langohr](https://github.com/michaelklishin/langohr), against
[CloudAMQP](https://www.cloudamqp.com) or any AMQP 0-9-1 broker such as
[LavinMQ](https://lavinmq.com) or RabbitMQ.

All code is in [`core.clj`](src/clojure_amqp_example/core.clj):

- `publish` publishes a persistent message and waits for the broker's confirm.
- `consume` consumes with manual acknowledgements and a prefetch of 10.
- `connect` connects using the `CLOUDAMQP_URL` environment variable. For
  `amqps://` URLs it passes the JVM's default `SSLContext` and enables hostname
  verification: without that, Langohr's Java client trusts any server
  certificate.

## Usage

Tool versions are in `mise.toml`; install them with [mise](https://mise.jdx.dev),
or use Java and [Leiningen](https://leiningen.org).

```sh
export CLOUDAMQP_URL="amqps://user:password@host/vhost"
lein run worker    # in one terminal
lein run publish   # in another
```

Without `CLOUDAMQP_URL` it connects to `amqp://guest:guest@localhost`.

## Tests

`lein test` publishes and consumes a message through a real broker on
localhost. CI runs it against LavinMQ.
