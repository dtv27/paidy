package forex.services.oneframe.interpreters

import cats.effect.Sync
import cats.implicits._
import forex.config.OneFrameConfig
import forex.domain.{ Currency, Price, Rate }
import forex.http.rates.Protocol.oneFrameRateDecoder
import forex.services.oneframe.OneFrameError.OneFrameLookupFailed
import forex.services.oneframe.Protocol.OneFramePayload
import forex.services.oneframe.{ ApiClientAlgebra, OneFrameError }
import org.http4s.Method.GET
import org.http4s.Uri.RegName
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s._

/** OneFrameLive provides an implementation of the ApiClientAlgebra to connect to the OneFrame API.
  *
  * @param config     the OneFrameConfig containing connection details
  * @param httpClient the HTTP client to be used for making API requests
  */
class OneFrameLive[F[_]: Sync](config: OneFrameConfig, httpClient: Client[F]) extends ApiClientAlgebra[F] {

  /** Retrieves all exchange rates from the OneFrame API.
    *
    * @return either a OneFrameError or a list of rates
    */
  def getAllRates: F[Either[OneFrameError, List[Rate]]] = {

    val ratesUri: Uri = Uri(
      authority = Uri.Authority(host = RegName(config.host), port = config.port.some).some,
      path = "/rates",
      query = Query.fromPairs(createCurrencyPairs(): _*)
    )

    val request = Request[F](
      method = GET,
      uri = ratesUri,
      headers = Headers.of(Header("token", config.token))
    )

    httpClient.expect[List[OneFramePayload]](request).attempt.map {
      case Right(payloads) =>
        payloads
          .traverse { payload =>
            val ratePair = Rate.Pair(payload.from, payload.to)
            val price    = payload.price
            Rate(ratePair, Price(price), payload.timestamp).asRight[OneFrameError]
          }
          .leftMap(_ => OneFrameLookupFailed)
      case Left(_) =>
        OneFrameLookupFailed.asLeft[List[Rate]]
    }
  }

  /** Creates a list of currency pairs for all supported currencies.
    *
    * @return a list of tuples containing the string "pair" and the concatenated currency pair
    */
  private def createCurrencyPairs(): List[(String, String)] =
    for {
      fromCurrency <- Currency.allCurrencies
      toCurrency <- Currency.allCurrencies
      if fromCurrency != toCurrency
      pair = fromCurrency.show + toCurrency.show
    } yield ("pair", pair)
}
