package effects

import cats.effect.Sync
import cats.syntax.all._
import java.util.UUID

trait GenUUID[F[_]]:
    
    def make: F[UUID]

object GenUUID:

    def apply[F[_]](using ev: GenUUID[F]): GenUUID[F] = ev

/*     def syncGenUUID[F[_]: Sync]: GenUUID[F] =
        new GenUUID[F]:
            def make: F[UUID] =
                Sync[F].delay(UUID.randomUUID) */

    given syncGenUUID[F[_]](using syncF: Sync[F]) as GenUUID[F]:
        def make: F[UUID] = syncF.delay(UUID.randomUUID())