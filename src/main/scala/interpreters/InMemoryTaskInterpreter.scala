package interpreters

import cats.effect._
import cats.syntax.all._
import cats.effect.concurrent.Ref
import scala.collection.immutable.HashMap
import scala.language.postfixOps
import java.util.UUID

import domain._
import algebras._
import effects.GenUUID.{given _}
import effects.GenZonedTimeDate.{given _}
import effects._

object InMemoryTaskInterpreter:
    def create[F[_]: Sync: GenUUID](state: Ref[F, HashMap[UUID, Task]]): TaskRepository[F] =

        new TaskRepository[F]:

            def tasks: F[Tasks] =
                state.get.map(m => Tasks(m))

            def tasks(tag: Tag): F[Tasks] =
                state.get.map( m => Tasks(m.filter { 
                    case (k, v) => v.tags.contains(tag)
                }))

            def create(task: Task): F[UUID] =
                for
                    id <- GenUUID[F].make
                    _  <- state.update(_.updated(id,task))
                yield id

            def read(id: UUID): F[Option[Task]] =
                state.get.map(_.get(id))

            def update(id: UUID, task: Task): F[Option[Task]] =
                state.update(_.updated(id, task)) >> read(id)

            def complete(id: UUID): F[Option[Task]] =
                for
                    date <- GenZonedTimeDate[F].make
                    optTask <- read(id)
                    updatedTask <- optTask match
                            case None => Sync[F].pure(None)
                            case Some(t) => update(id, t.complete(date)) 
                yield updatedTask

            def delete(id: UUID): F[Unit] =
                state.update(_.removed(id))

            def tags: F[Tags] =
                state.get.map(m => Tags(m.values.flatMap(t => t.tags).toList.distinct))
                
                
