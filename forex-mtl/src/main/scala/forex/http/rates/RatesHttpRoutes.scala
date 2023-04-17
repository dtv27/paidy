package forex.http
package rates

import cats.effect.Sync
import cats.implicits._
import forex.domain.Currency
import forex.programs.RatesProgram
import forex.programs.rates.Protocol.GetRatesRequest
import org.http4s.{ HttpRoutes }
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

/** HTTP routes for the rates API.
  *
  * @param rates the RatesProgram instance to handle the rate requests
  */
class RatesHttpRoutes[F[_]: Sync](rates: RatesProgram[F]) extends Http4sDsl[F] {

  import Converters._
  import Protocol._
  import QueryParams._

  private[http] val prefixPath = "/rates"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? FromQueryParam(fromOpt) +& ToQueryParam(toOpt) =>
      val from: Option[Currency] = fromOpt.flatMap(_.toOption)
      val to: Option[Currency]   = toOpt.flatMap(_.toOption)

      (from, to) match {
        case (Some(from), Some(to)) =>
          val request = GetRatesRequest(from, to)
          rates.get(request).flatMap {
            case Left(error) =>
              BadRequest(RatesHttpError("INTERNAL_ERROR", s"An error occurred: ${error.getMessage}"))
            case Right(rate) =>
              Ok(rate.asGetApiResponse)
          }
        case (None, None) =>
          BadRequest(RatesHttpError("INVALID_QUERY_PARAMETERS", "Invalid query parameters [from, to]"))
        case (None, _) =>
          BadRequest(RatesHttpError("INVALID_FROM_CURRENCY", "The given from currency is not valid"))
        case (_, None) =>
          BadRequest(RatesHttpError("INVALID_TO_CURRENCY", "The given to currency is not valid"))
      }
  }

  /** The combined HTTP routes for the rates API. */
  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
