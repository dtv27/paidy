package forex

import cats.effect.concurrent.Ref
import cats.effect.{ ExitCode, IO, IOApp }
import cats.implicits.catsSyntaxFlatMapOps
import forex.config.Config
import forex.domain.Rate
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val appStream = for {
      config <- Config.stream[IO]("app")
      // Initialize an empty in-memory cache of exchange rates
      cache <- Stream.eval(Ref.of[IO, Map[Rate.Pair, Rate]](Map.empty))
      httpClient <- BlazeClientBuilder[IO](executionContext).stream
      app = new Application[IO](config, httpClient, cache)
      // Schedule the exchange rate retrieval task to run continuously
      scheduledRetrievalTaskStream = app.scheduleSyncRate.foreverM
      // Start the HTTP server and merge it with the scheduled retrieval task stream
      _ <- app.streamApplication(executionContext).merge(scheduledRetrievalTaskStream)
    } yield ()

    appStream.compile.drain.as(ExitCode.Success)
  }

}
