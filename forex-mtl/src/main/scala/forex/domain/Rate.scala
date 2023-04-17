package forex.domain

/** Represents an exchange rate between two currencies.
  *
  * @param pair a currency pair consisting of 'from' and 'to' currencies
  * @param price the exchange rate for the currency pair
  * @param timestamp the timestamp indicating when the exchange rate was last updated
  */
case class Rate(
    pair: Rate.Pair,
    price: Price,
    timestamp: Timestamp
)

object Rate {

  /** Represents a pair of currencies for an exchange rate.
    *
    * @param from the base currency in the pair
    * @param to   the counter currency in the pair
    */
  final case class Pair(
      from: Currency,
      to: Currency
  )
}
