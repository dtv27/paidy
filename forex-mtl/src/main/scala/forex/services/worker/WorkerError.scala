package forex.services.worker

sealed trait WorkerError extends Throwable

object WorkerError {
  final case class SyncFailed(msg: String) extends WorkerError
}
