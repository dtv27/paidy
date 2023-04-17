package forex.services.rates.interpreters

import cats.effect.Sync
import cats.implicits._
import forex.domain.Rate
import forex.services.CacheRepository
import forex.services.rates.RatesError.RateLookUpFailed
import forex.services.rates.{ RatesError, RepositoryLookupAlgebra }

/** RateLookup provides an implementation of the RepositoryLookupAlgebra for retrieving exchange rates.
  *
  * @param cache the CacheRepository instance for retrieving rates from the cache
  */
class RateLookup[F[_]: Sync](cache: CacheRepository[F]) extends RepositoryLookupAlgebra[F] {
  def get(pair: Rate.Pair): F[Either[RatesError, Rate]] =
    cache
      .get(pair)
      .map(_.leftMap(_ => RateLookUpFailed(s"Unable to get rates for pair: ${pair.from.show}->${pair.to.show}")))

}
