package forex.services.cache

import forex.domain.Rate

trait CacheAlgebra[F[_]] {
  def get(key: Rate.Pair): F[Either[CacheError, Rate]]

  def put(key: Rate.Pair, value: Rate): F[Either[CacheError, Unit]]
}
