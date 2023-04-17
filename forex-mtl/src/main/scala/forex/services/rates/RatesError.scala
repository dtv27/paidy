package forex.services.rates

sealed trait RatesError extends Throwable

object RatesError {
  final case class RateLookUpFailed(message: String) extends RatesError
}
