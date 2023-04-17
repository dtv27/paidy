package forex.services.rates

import forex.domain.Rate

trait RepositoryLookupAlgebra[F[_]] {
  def get(pair: Rate.Pair): F[RatesError Either Rate]
}
