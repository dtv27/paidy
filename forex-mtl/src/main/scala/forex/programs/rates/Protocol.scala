package forex.programs.rates

import forex.domain.Currency

/** Protocol definitions for the rates program. */
object Protocol {

  /** A request to get rates for a specified currency pair.
    *
    * @param from from currency
    * @param to   to currency
    */
  final case class GetRatesRequest(
      from: Currency,
      to: Currency
  )

}
