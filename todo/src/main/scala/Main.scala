package todo

import cats.effect._
import cats.implicits._
import fs2.Stream
import org.http4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.staticcontent._
import org.http4s.server.{Router, Server}
import org.http4s.syntax.kleisli._
import org.http4s.server.middleware.CORS

import api.TaskRoutes
import interpreters.InMemoryTaskInterpreter

object Main extends IOApp:

  object AssetService:
    def service(blocker: Blocker)(using cs: ContextShift[IO]): HttpRoutes[IO] =
      fileService(FileService.Config("./assets", blocker))

  private def app(blocker: Blocker): HttpApp[IO] =
    Router.define("/api" -> new TaskRoutes[IO](InMemoryTaskInterpreter.create[IO]).httpRoutes)
      (AssetService.service(blocker)).orNotFound

  private def server(blocker: Blocker): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost("0.0.0.0")
      .withPort(3000)
      .withHttpApp(app(blocker))
      .build

  private val program: Stream[IO, Unit] =
    for
      blocker <- Stream.resource(Blocker[IO])
      server <- Stream.resource(server(blocker))
      _ <- Stream.eval(IO(println("Started server on: ${server.address}")))
      _ <- Stream.never[IO].covaryOutput[Unit]
    yield ()

  def run(args: List[String]): IO[ExitCode] =
    program.compile.drain.as(ExitCode.Success)
