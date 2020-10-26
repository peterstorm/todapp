package todo

import cats.effect._
import cats.implicits._
import cats.effect.concurrent.Ref
import fs2.Stream
import org.http4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.staticcontent._
import org.http4s.server.{Router, Server}
import org.http4s.syntax.kleisli._
import scala.collection.immutable.HashMap
import java.util.UUID

import api.TaskRoutes
import interpreters.InMemoryTaskInterpreter
import domain.Task
import delivery._

object Main extends IOApp:

  private val program: Stream[IO, Unit] =
    for
      blocker <- Stream.resource(Blocker[IO])
      ref <- Stream.eval(Ref.of[IO, HashMap[UUID, Task]](HashMap.empty))
      server <- Stream.resource(InMemoryServer.create(InMemoryHttpApp.create(blocker, ref)))
      _ <- Stream.eval(IO(println("Started server...")))
      _ <- Stream.never[IO].covaryOutput[Unit]
    yield ()

  def run(args: List[String]): IO[ExitCode] =
    program.compile.drain.as(ExitCode.Success)
