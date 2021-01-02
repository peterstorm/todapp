package delivery

import cats.effect._
import cats.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s._
import org.http4s.server.{Server}
import scala.collection.immutable.HashMap
import java.util.UUID

import domain.Task

object InMemoryServer:
    def create[F[_]: Sync: ContextShift: Concurrent: Timer](app: HttpApp[F]): Resource[F, Server] =
        EmberServerBuilder
            .default[F]
            .withHost("0.0.0.0")
            .withPort(3000)
            .withHttpApp(app)
            .build