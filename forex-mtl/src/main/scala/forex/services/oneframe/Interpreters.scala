package forex.services.oneframe

import cats.effect.Sync
import forex.config.OneFrameConfig
import forex.services.oneframe.interpreters.OneFrameLive
import org.http4s.client.Client

object Interpreters {

  def live[F[_] : Sync](config: OneFrameConfig, httpClient: Client[F]): ApiClientAlgebra[F] =
    new OneFrameLive[F](config, httpClient)

}
