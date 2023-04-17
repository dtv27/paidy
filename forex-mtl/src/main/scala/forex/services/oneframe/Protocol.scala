package forex.services.oneframe

import forex.domain.{ Currency, Timestamp }
import io.circe.generic.extras.JsonKey

/** Protocol definitions for the OneFrame service. */
object Protocol {

  final case class OneFramePayload(
      from: Currency,
      to: Currency,
      price: Double,
      @JsonKey("time_stamp") val timestamp: Timestamp
  )
}
