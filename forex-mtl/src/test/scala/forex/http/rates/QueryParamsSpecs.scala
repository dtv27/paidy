package forex.http.rates

import cats.data.Validated.Valid
import forex.domain.Currency
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class QueryParamsSpec extends AnyFlatSpec with Matchers {

  "FromQueryParam and ToQueryParam" should "match and decode valid Currency query parameters" in {
    val queryParams = Map("from" -> List("USD"), "to" -> List("EUR"))

    val fromResult = QueryParams.FromQueryParam.unapply(queryParams)
    val toResult   = QueryParams.ToQueryParam.unapply(queryParams)

    fromResult shouldEqual Some(Some(Valid(Currency.USD)))
    toResult shouldEqual Some(Some(Valid(Currency.EUR)))
  }
}
