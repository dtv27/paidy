package forex

import cats.effect.concurrent.Ref
import cats.effect.{ ConcurrentEffect, Sync, Timer }
import forex.config.ApplicationConfig
import forex.domain.Rate
import forex.services.{ CacheRepository, OneFrameService, Worker, WorkerService }
import fs2.Stream
import org.http4s.client.Client
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

/** Initializes the services and streams required for the application.
  *
  * @param config     the application configuration
  * @param httpClient the HTTP client for making external API calls
  * @param cache      a Ref to the in-memory cache of exchange rates
  */
class Application[F[_]: ConcurrentEffect: Timer](
    config: ApplicationConfig,
    httpClient: Client[F],
    cache: Ref[F, Map[Rate.Pair, Rate]],
) {

  private val cacheRepository: CacheRepository[F] = CacheRepository.cache(cache)
  private val oneFrameService: OneFrameService[F] = OneFrameService.live(config.oneFrame, httpClient)
  private val rateRefresher: Worker[F]            = WorkerService.worker(oneFrameService, cacheRepository)

  /** Schedules the synchronization of exchange rates at the configured interval.
    */
  def scheduleSyncRate: Stream[F, Unit] =
    Stream.eval(rateRefresher.syncRates()).drain ++ Stream.sleep(config.worker.updateInterval)

  /** Creates and starts the HTTP server, as well as initializes the required services and modules.
    *
    * @param ec the execution context for the server
    */
  def streamApplication(ec: ExecutionContext): Stream[F, Unit] =
    for {
      module <- Stream.eval(Sync[F].delay(new Module[F](config, cacheRepository)))
      _ <- BlazeServerBuilder[F](ec)
            .bindHttp(config.http.port, config.http.host)
            .withHttpApp(module.httpApp)
            .serve
    } yield ()
}
