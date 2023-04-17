package forex.programs.rates

import forex.services.rates.RatesError

/** Represents errors in the rates program. */
sealed trait RatesProgramError extends RuntimeException

object RatesProgramError {

  /** Converts a RatesError to a RatesProgramError.
    *
    * @param error the RatesError instance to be converted
    * @return the corresponding RatesProgramError instance
    */
  def toProgramError(error: RatesError): RatesProgramError = error match {
    case RatesError.RateLookUpFailed(message: String) => RatesProgramError.RateLookupFailed(Some(message))
    case _                                            => RatesProgramError.RateFailed("An unexpected error occurred")

  }

  /** A rate lookup failed error.
    *
    * @param msg the error message
    */
  final case class RateFailed(msg: String) extends RatesProgramError

  case class RateLookupFailed(message: Option[String]) extends RatesProgramError {
    override def getMessage: String = message.getOrElse("Rate Request Lookup failed")
  }
}
