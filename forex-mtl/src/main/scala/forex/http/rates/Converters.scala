package forex.http.rates

import forex.domain.Rate

/** Provides type converters for working with the HTTP rates API. */
object Converters {
  import forex.http.rates.Protocol.GetApiResponse

  /** Adds conversion functionality to the Rate type.
    *
    * @param rate the Rate instance to be extended with the conversion method
    */
  private[rates] implicit class GetApiResponseOps(val rate: Rate) extends AnyVal {

    /** Converts a Rate instance to a GetApiResponse instance.
      *
      * @return a GetApiResponse representation of the given Rate
      */
    def asGetApiResponse: GetApiResponse =
      GetApiResponse(
        from = rate.pair.from,
        to = rate.pair.to,
        price = rate.price,
        timestamp = rate.timestamp
      )
  }

}
