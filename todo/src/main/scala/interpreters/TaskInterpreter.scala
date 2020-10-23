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
import effects._

object InMemoryTaskInterpreter:

    def create[F[_]: Sync: GenUUID]: TaskAlgebra[F] =
        new TaskAlgebra[F]:
            private val state = Ref.of[F, HashMap[UUID, Task]](HashMap.empty)

            def tasks: F[Tasks] =
                state.flatMap(_.get.map(m => Tasks(m)))

            def tasks(tag: Tag): F[Tasks] =
                state.flatMap(_.get.map( m => Tasks(m.filter { 
                    case (k, v) => v.tags.contains(tag)
                })))

            def create(task: Task): F[UUID] =
                for
                    id <- GenUUID[F].make
                    _  <- state.flatMap(_.update(_.updated(id,task)))
                yield id

            def read(id: UUID): F[Option[Task]] =
                state.flatMap(_.get.map(_.get(id)))

            def update(id: UUID, task: Task): F[Option[Task]] =
                state.flatMap(_.update(_.+ (id -> task)))
                read(id)

            def complete(id: UUID): F[Option[Task]] =
                for
                    completedOptTask <- read(id).map(_.map(_.complete))
                    updatedTask <- completedOptTask match
                            case None => Sync[F].pure(None)
                            case Some(t) => update(id, t) 
                yield updatedTask

            def delete(id: UUID): F[Unit] =
               state.flatMap(_.update(_.removed(id)))

            def tags: F[Tags] =
                state.flatMap(_.get.map(m => Tags(m.values.flatMap(t => t.tags).toList.distinct)))
                
                
