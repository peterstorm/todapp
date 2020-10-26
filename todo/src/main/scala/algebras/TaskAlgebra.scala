package algebras

import java.util.UUID

import domain._

trait TaskAlgebra[F[_]]:
    def tasks: F[Tasks]

    def tasks(tag: Tag): F[Tasks]

    def create(task: Task): F[UUID]

    def read(id: UUID): F[Option[Task]]

    def update(id: UUID, task: Task): F[Option[Task]]

    def delete(id: UUID): F[Unit]

    def complete(id: UUID): F[Option[Task]]

    def tags: F[Tags]