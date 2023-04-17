package forex.http.rates

import cats.effect.IO
import forex.domain.{ Currency, Price, Rate, Timestamp }
import forex.programs.RatesProgram
import forex.programs.rates.Protocol.GetRatesRequest
import org.http4s._
import org.http4s.implicits._
import org.mockito.cats.IdiomaticMockitoCats
import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RatesHttpRoutesSpec
    extends AnyFlatSpec
    with Matchers
    with IdiomaticMockito
    with ArgumentMatchersSugar
    with IdiomaticMockitoCats {

  "RatesHttpRoutes" should "return rate for valid GET request" in {
    val mockRatesProgram: RatesProgram[IO] = mock[RatesProgram[IO]]
    val rate                               = Rate(Rate.Pair(Currency.USD, Currency.JPY), Price(100.0), Timestamp.now)

    mockRatesProgram.get(GetRatesRequest(Currency.USD, Currency.JPY)) returns IO.pure(Right(rate))

    val routes = new RatesHttpRoutes[IO](mockRatesProgram).routes.orNotFound

    val request  = Request[IO](Method.GET, uri"/rates?from=USD&to=JPY")
    val response = routes.run(request).unsafeRunSync()

    response.status shouldBe Status.Ok
    response.as[String].unsafeRunSync() should include("USD")
    response.as[String].unsafeRunSync() should include("JPY")
    response.as[String].unsafeRunSync() should include("100.0")
  }

  it should "return BadRequest for invalid GET request" in {
    val mockRatesProgram: RatesProgram[IO] = mock[RatesProgram[IO]]
    val routes                             = new RatesHttpRoutes[IO](mockRatesProgram).routes.orNotFound

    val request  = Request[IO](Method.GET, uri"/rates?from=INVALID&to=JPY")
    val response = routes.run(request).unsafeRunSync()

    response.status shouldBe Status.BadRequest
    response.as[String].unsafeRunSync() should include("INVALID_FROM_CURRENCY")
  }

  it should "return BadRequest for missing GET request parameters" in {
    val mockRatesProgram: RatesProgram[IO] = mock[RatesProgram[IO]]
    val routes                             = new RatesHttpRoutes[IO](mockRatesProgram).routes.orNotFound

    val request  = Request[IO](Method.GET, uri"/rates")
    val response = routes.run(request).unsafeRunSync()

    response.status shouldBe Status.BadRequest
    response.as[String].unsafeRunSync() should include("INVALID_QUERY_PARAMETERS")
  }
}
