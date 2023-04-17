package forex.services.cache

import cats.Applicative
import cats.effect.concurrent.Ref
import forex.domain.Rate
import forex.services.cache.interpreters.InMemoryCache

object Interpreters {
  def cache[F[_]: Applicative](cache: Ref[F, Map[Rate.Pair, Rate]]): CacheAlgebra[F] = new InMemoryCache(cache)

}
