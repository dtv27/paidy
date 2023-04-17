package forex.services.cache.interpreters

import cats.effect.IO
import cats.effect.concurrent.Ref
import forex.domain.{ Currency, Price, Rate, Timestamp }
import forex.services.cache.CacheError.CacheMissException
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InMemoryCacheSpec extends AnyFlatSpec with Matchers {

  "put" should "insert a new key-value pair into the cache" in {
    val ratePair = Rate.Pair(Currency.USD, Currency.EUR)
    val rate     = Rate(ratePair, Price(BigDecimal(0.85)), Timestamp.now)
    val ref      = Ref.unsafe[IO, Map[Rate.Pair, Rate]](Map.empty)

    val cache = new InMemoryCache[IO](ref)

    val putResult = cache.put(ratePair, rate).unsafeRunSync()

    putResult.isRight shouldBe true

    val getResult = cache.get(ratePair).unsafeRunSync()

    getResult.isRight shouldBe true
    getResult.getOrElse(Rate(ratePair, Price(BigDecimal(0)), Timestamp.now)) shouldEqual rate
  }

  "get" should "return the value associated with the given key from the cache" in {
    val ratePair = Rate.Pair(Currency.USD, Currency.EUR)
    val rate     = Rate(ratePair, Price(BigDecimal(0.85)), Timestamp.now)
    val ref      = Ref.unsafe[IO, Map[Rate.Pair, Rate]](Map(ratePair -> rate))

    val cache = new InMemoryCache[IO](ref)

    val result = cache.get(ratePair).unsafeRunSync()

    result.isRight shouldBe true
    result.getOrElse(Rate(ratePair, Price(BigDecimal(0)), Timestamp.now)) shouldEqual rate
  }

  it should "return a CacheMissException error when the key is not found" in {
    val ratePair = Rate.Pair(Currency.USD, Currency.EUR)
    val ref      = Ref.unsafe[IO, Map[Rate.Pair, Rate]](Map.empty)

    val cache = new InMemoryCache[IO](ref)

    val result = cache.get(ratePair).unsafeRunSync()

    result.isLeft shouldBe true
    result.swap.getOrElse(CacheMissException) shouldBe CacheMissException
  }
}
