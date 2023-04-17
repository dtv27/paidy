package forex.services.cache.interpreters

import cats.Functor
import cats.effect.concurrent.Ref
import cats.implicits._
import forex.domain.Rate
import forex.services.cache.{ CacheAlgebra, CacheError }

/** In-memory implementation of the CacheAlgebra.
  *
  * @param cache a reference to the in-memory cache map
  */
class InMemoryCache[F[_]: Functor](cache: Ref[F, Map[Rate.Pair, Rate]]) extends CacheAlgebra[F] {

  /** Puts a new key-value pair into the cache.
    *
    * @param key   the rate pair to be used as a key
    * @param value the rate to be stored in the cache
    * @return either a cache error or unit
    */
  def put(key: Rate.Pair, value: Rate): F[Either[CacheError, Unit]] =
    cache.update(_.updated(key, value)).map(_.asRight)

  /** Retrieves the value associated with the given key from the cache.
    *
    * @param key the rate pair to be used as a key
    * @return either a cache error or the rate
    */
  def get(key: Rate.Pair): F[Either[CacheError, Rate]] =
    cache.get.map(
      _.get(key).toRight(CacheError.CacheMissException(s"Cache miss for key: ${key.from.show}->${key.to.show}"))
    )
}
