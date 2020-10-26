package delivery

import cats.effect._
import cats.implicits._
import cats.effect.concurrent.Ref
import org.http4s._
import org.http4s.syntax.kleisli._
import org.http4s.server.{Router}
import scala.collection.immutable.HashMap
import java.util.UUID

import api.TaskRoutes
import domain.Task
import interpreters.InMemoryTaskInterpreter

object InMemoryHttpApp:
    def create[F[_]: Concurrent: ContextShift](blocker: Blocker, ref: Ref[F, HashMap[UUID, Task]]): HttpApp[F] =
        Router.define("/api" -> new TaskRoutes[F](InMemoryTaskInterpreter.create(ref)).httpRoutes)
                        (AssetService.service(blocker)).orNotFound