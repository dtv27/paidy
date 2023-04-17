package forex.services.worker

trait WorkerAlgebra[F[_]] {
  def syncRates(): F[Either[WorkerError, Unit]]
}
