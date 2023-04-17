package forex.programs.rates

import cats.Functor
import cats.data.EitherT
import forex.domain.Rate
import forex.programs.rates.Protocol.GetRatesRequest
import forex.programs.rates.RatesProgramError.toProgramError
import forex.services.RatesService

/** Implementation of the rates algebra.
  *
  * @param repositoryLookup the RatesService instance to handle currency rate lookups
  */
class Program[F[_]: Functor](repositoryLookup: RatesService[F]) extends RatesAlgebra[F] {

  def get(request: GetRatesRequest): F[Either[RatesProgramError, Rate]] =
    EitherT(repositoryLookup.get(Rate.Pair(request.from, request.to))).leftMap(toProgramError).value

}

object Program {

  /** Creates a new instance of the Program class.
    *
    * @param repositoryLookup the RatesService instance to handle rate lookups
    */
  def apply[F[_]: Functor](repositoryLookup: RatesService[F]): RatesAlgebra[F] = new Program[F](repositoryLookup)
}
