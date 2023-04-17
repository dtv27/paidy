package forex

import cats.effect.{ Concurrent, Timer }
import forex.config.ApplicationConfig
import forex.http.rates.RatesHttpRoutes
import forex.programs.RatesProgram
import forex.services.{ CacheRepository, RatesService, RatesServices }
import org.http4s.implicits._
import org.http4s.server.middleware.{ AutoSlash, Timeout }
import org.http4s.{ HttpApp, HttpRoutes }

class Module[F[_]: Concurrent: Timer](config: ApplicationConfig, cache: CacheRepository[F]) {

  private val ratesService: RatesService[F]  = RatesServices.rateLookup(cache)
  private val ratesProgram: RatesProgram[F]  = RatesProgram[F](ratesService)
  private val ratesHttpRoutes: HttpRoutes[F] = new RatesHttpRoutes[F](ratesProgram).routes

  type PartialMiddleware = HttpRoutes[F] => HttpRoutes[F]
  type TotalMiddleware   = HttpApp[F] => HttpApp[F]

  private val routesMiddleware: PartialMiddleware = { http: HttpRoutes[F] =>
    AutoSlash(http)
  }
  private val appMiddleware: TotalMiddleware = { http: HttpApp[F] =>
    Timeout(config.http.timeout)(http)
  }

  private val http: HttpRoutes[F] = ratesHttpRoutes

  val httpApp: HttpApp[F] = appMiddleware(routesMiddleware(http).orNotFound)

}
