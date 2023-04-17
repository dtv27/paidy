package forex.services.oneframe

sealed trait OneFrameError extends Throwable

object OneFrameError {
  final case object OneFrameLookupFailed extends OneFrameError
}
