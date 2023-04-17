package forex.services.worker.interpreters

import cats.data.EitherT
import cats.effect.Sync
import cats.implicits._
import forex.services.cache.CacheAlgebra
import forex.services.oneframe.ApiClientAlgebra
import forex.services.worker.WorkerError.SyncFailed
import forex.services.worker.{ WorkerAlgebra, WorkerError }

/** RateRefresher provides an implementation of the WorkerAlgebra for synchronizing exchange rates.
  *
  * @param oneFrameService  the ApiClientAlgebra instance for fetching rates from the OneFrame API
  * @param cacheRepository  the CacheAlgebra instance for storing rates in the cache
  */
class RateRefresher[F[_]: Sync](oneFrameService: ApiClientAlgebra[F], cacheRepository: CacheAlgebra[F])
    extends WorkerAlgebra[F] {

  /** Synchronizes exchange rates by fetching them from the OneFrame API and storing them in the cache.
    *
    * @return either a WorkerError or Unit
    */
  def syncRates(): F[Either[WorkerError, Unit]] =
    (for {
      allRates <- EitherT(oneFrameService.getAllRates).leftMap(buildSyncFailedError)
      _ <- allRates.traverse { rate =>
            val ratePair = rate.pair
            EitherT(cacheRepository.put(ratePair, rate))
              .leftMap(buildSyncFailedError)
          }
    } yield ()).value.map(_.leftMap(identity))

  /** Builds a SyncFailed error with the given Throwable.
    *
    * @param error the Throwable to use as the error message
    * @return a SyncFailed instance
    */
  private def buildSyncFailedError(error: Throwable): SyncFailed =
    SyncFailed(error.getMessage)
}
