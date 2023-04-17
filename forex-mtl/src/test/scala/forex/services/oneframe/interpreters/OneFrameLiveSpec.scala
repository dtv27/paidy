package forex.services.oneframe.interpreters

import cats.effect.IO
import forex.config.OneFrameConfig
import forex.domain.{ Currency, Timestamp }
import forex.services.oneframe.OneFrameError
import forex.services.oneframe.Protocol.OneFramePayload
import org.http4s.Request
import org.http4s.client.Client
import org.mockito.cats.IdiomaticMockitoCats
import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OneFrameLiveSpec
    extends AnyFlatSpec
    with Matchers
    with IdiomaticMockito
    with ArgumentMatchersSugar
    with IdiomaticMockitoCats {

  "getAllRates" should "return a list of rates" in {
    val config               = OneFrameConfig("host", 80, "token")
    val httpClientMock       = mock[Client[IO]]
    val apiClientInterpreter = new OneFrameLive[IO](config, httpClientMock)

    val payload1 = OneFramePayload(Currency.USD, Currency.EUR, 0.85, Timestamp.now)
    val payload2 = OneFramePayload(Currency.EUR, Currency.USD, 1.18, Timestamp.now)

    httpClientMock.expect[List[OneFramePayload]](any[Request[IO]])(any) returns IO.pure(List(payload1, payload2))

    val result = apiClientInterpreter.getAllRates.unsafeRunSync()

    result.isRight shouldBe true
    result.getOrElse(Nil).size shouldEqual 2
  }

  it should "return a OneFrameLookupFailed error" in {
    val config               = OneFrameConfig("host", 80, "token")
    val httpClientMock       = mock[Client[IO]]
    val apiClientInterpreter = new OneFrameLive[IO](config, httpClientMock)

    httpClientMock.expect[List[OneFramePayload]](any[Request[IO]])(any) returns IO.raiseError(
      new RuntimeException("API call failed")
    )

    val result = apiClientInterpreter.getAllRates.unsafeRunSync()

    result.isLeft shouldBe true
    result.swap.getOrElse(OneFrameError.OneFrameLookupFailed) shouldBe OneFrameError.OneFrameLookupFailed
  }

}
