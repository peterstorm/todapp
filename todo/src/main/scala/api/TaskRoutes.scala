package api 

import io.circe.syntax._
import cats.effect._
import cats.syntax.all._
import org.http4s._
import org.http4s.circe._
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.http4s.dsl._

import algebras._
import domain._

final class TaskRoutes[F[_]: Sync](algebra: TaskAlgebra[F]) extends Http4sDsl[F]:

/*     given decodeProduct as EntityDecoder[F, Task] = jsonOf
    given encoderProduct[F[_] : Applicative] as EntityEncoder[F, Task] = jsonEncoder */

    val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
        case GET -> Root / "task" / UUIDVar(id) =>
                algebra.read(id) >>= (_.fold(NotFound())(t => Ok(t)))

        case req @ POST -> Root / "task" =>
            req
              .as[Task] >>= (t => algebra.create(t)) >>= (id => Ok(id))

        case req @ POST -> Root / "task" / UUIDVar(id) =>
            req
              .as[Task] >>= (t => algebra.update(id, t)) >>= (_.fold(NotFound())(t => Ok(t)))
        
        case DELETE -> Root / "task" / UUIDVar(id) =>
            algebra.delete(id) *> Ok()

        case POST -> Root / "task" / UUIDVar(id) / "complete" =>
            algebra.complete(id) >>= (_.fold(NotFound())(t => Ok(t)))

        case GET -> Root / "tasks" =>
            algebra.tasks >>= (tasks => Ok(tasks))

        case GET -> Root / "tasks" / tag =>
            algebra.tasks(Tag(tag)) >>= (tasks => Ok(tasks))

        case GET -> Root / "tags" =>
            algebra.tags >>= (tags => Ok(tags))
    }

