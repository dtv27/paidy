package forex.services.rates

import cats.effect.Sync
import forex.services.CacheRepository
import forex.services.rates.interpreters.RateLookup

object Interpreters {

  def rateLookup[F[_]: Sync](cache: CacheRepository[F]): RepositoryLookupAlgebra[F] = new RateLookup[F](cache)

}
