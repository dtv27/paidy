package forex.config

import scala.concurrent.duration.FiniteDuration

/** Configuration for the entire application.
  *
  * @param http     configuration for the HTTP server
  * @param oneFrame configuration for the OneFrame service
  * @param worker   configuration for the rate worker
  */
final case class ApplicationConfig(
    http: HttpConfig,
    oneFrame: OneFrameConfig,
    worker: WorkerConfig,
)

/** Configuration for the HTTP server.
  *
  * @param host    the server's host
  * @param port    the server's port
  * @param timeout the server's request timeout duration, default to 10 seconds
  */
final case class HttpConfig(
    host: String,
    port: Int,
    timeout: FiniteDuration = FiniteDuration(10, "seconds"),
)

/** Configuration for the OneFrame service.
  *
  * @param host  the service's host
  * @param port  the service's port
  * @param token the service's API token
  */
final case class OneFrameConfig(
    host: String,
    port: Int,
    token: String,
)

/** Configuration for the rate worker.
  *
  * @param updateInterval the interval between rate updates
  */
final case class WorkerConfig(
    updateInterval: FiniteDuration,
)
