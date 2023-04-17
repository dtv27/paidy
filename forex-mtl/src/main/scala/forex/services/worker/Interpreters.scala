package forex.services.worker

import cats.effect.Sync
import forex.services.{ CacheRepository, OneFrameService }
import forex.services.worker.interpreters.RateRefresher

object Interpreters {

  def worker[F[_]: Sync](oneFrameService: OneFrameService[F], cacheRepository: CacheRepository[F]): WorkerAlgebra[F] =
    new RateRefresher[F](oneFrameService, cacheRepository)

}
