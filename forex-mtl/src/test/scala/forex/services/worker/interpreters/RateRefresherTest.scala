package forex.services.worker.interpreters

import cats.effect.IO
import cats.effect.concurrent.Ref
import forex.domain.{ Currency, Price, Rate, Timestamp }
import forex.services.cache.CacheAlgebra
import forex.services.cache.interpreters.InMemoryCache
import forex.services.oneframe.ApiClientAlgebra
import forex.services.worker.WorkerError.SyncFailed
import org.mockito.IdiomaticMockito
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RateRefresherSpec extends AnyFlatSpec with Matchers with IdiomaticMockito {

  "syncRates" should "synchronize exchange rates and store them in the cache" in {
    val oneFrameService = mock[ApiClientAlgebra[IO]]
    val ref             = Ref.unsafe[IO, Map[Rate.Pair, Rate]](Map.empty)
    val cacheRepository = new InMemoryCache[IO](ref)

    val worker = new RateRefresher[IO](oneFrameService, cacheRepository)

    val ratePair = Rate.Pair(Currency.USD, Currency.EUR)
    val rate     = Rate(ratePair, Price(BigDecimal(0.85)), Timestamp.now)

    oneFrameService.getAllRates returns IO.pure(Right(List(rate)))

    val result = worker.syncRates().unsafeRunSync()

    result.isRight shouldBe true

    val cacheContent = ref.get.unsafeRunSync()
    cacheContent.get(ratePair) shouldEqual Some(rate)
  }

  it should "return a SyncFailed error when fetching rates fails" in {
    val oneFrameService = mock[ApiClientAlgebra[IO]]
    val cacheRepository = mock[CacheAlgebra[IO]]

    val worker = new RateRefresher[IO](oneFrameService, cacheRepository)

    oneFrameService.getAllRates returns IO.raiseError(new RuntimeException("Fetching rates failed"))

    val result = worker.syncRates().unsafeRunSync()

    result.isLeft shouldBe true
    result.swap.getOrElse(SyncFailed("")) should matchPattern { case SyncFailed("Fetching rates failed") => }
  }

  it should "return a SyncFailed error when storing rates fails" in {
    val oneFrameService = mock[ApiClientAlgebra[IO]]
    val cacheRepository = mock[CacheAlgebra[IO]]

    val worker = new RateRefresher[IO](oneFrameService, cacheRepository)

    val ratePair = Rate.Pair(Currency.USD, Currency.EUR)
    val rate     = Rate(ratePair, Price(BigDecimal(0.85)), Timestamp.now)

    oneFrameService.getAllRates returns IO.pure(Right(List(rate)))
    cacheRepository.put(ratePair, rate) returns IO.raiseError(new RuntimeException("Storing rates failed"))

    val result = worker.syncRates().unsafeRunSync()

    result.isLeft shouldBe true
    result.swap.getOrElse(SyncFailed("")) should matchPattern { case SyncFailed("Storing rates failed") => }
  }
}
