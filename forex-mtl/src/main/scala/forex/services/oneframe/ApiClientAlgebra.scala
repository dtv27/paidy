package forex.services.oneframe

import forex.domain.Rate

trait ApiClientAlgebra[F[_]] {
  def getAllRates: F[Either[OneFrameError, List[Rate]]]
}
