package forex.http.rates

import cats.implicits._
import forex.domain.Currency
import org.http4s.dsl.impl.OptionalValidatingQueryParamDecoderMatcher
import org.http4s.{ ParseFailure, QueryParamDecoder }

import scala.util.Try

/** Query parameters and their decoders for the rates API. */
object QueryParams {

  private[http] implicit val currencyQueryParam: QueryParamDecoder[Currency] =
    QueryParamDecoder[String].emap(
      curr =>
        Try(Currency.fromString(curr)).toEither
          .leftMap { err =>
            ParseFailure(err.getMessage, err.getMessage)
        }
    )

  object FromQueryParam extends OptionalValidatingQueryParamDecoderMatcher[Currency]("from")
  object ToQueryParam extends OptionalValidatingQueryParamDecoderMatcher[Currency]("to")

}
