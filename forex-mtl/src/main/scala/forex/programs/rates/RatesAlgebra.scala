package forex.programs.rates

import forex.domain.Rate
import forex.programs.rates.Protocol.GetRatesRequest

trait RatesAlgebra[F[_]] {
  def get(request: GetRatesRequest): F[Either[RatesProgramError, Rate]]
}
