package forex

import forex.services.oneframe.ApiClientAlgebra
import forex.services.rates.RepositoryLookupAlgebra

package object services {
  final val RatesServices = rates.Interpreters
  type RatesService[F[_]] = RepositoryLookupAlgebra[F]

  type OneFrameService[F[_]] = ApiClientAlgebra[F]
  final val OneFrameService = oneframe.Interpreters

  type CacheRepository[F[_]] = cache.CacheAlgebra[F]
  final val CacheRepository = cache.Interpreters

  type Worker[F[_]] = worker.WorkerAlgebra[F]
  final val WorkerService = worker.Interpreters

}
