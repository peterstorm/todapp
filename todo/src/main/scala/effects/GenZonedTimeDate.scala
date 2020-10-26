package effects

import cats.effect.Sync
import cats.syntax.all._
import java.time.ZonedDateTime

trait GenZonedTimeDate[F[_]]:
    def make: F[ZonedDateTime]

object GenZonedTimeDate:
    def apply[F[_]](using ev: GenZonedTimeDate[F]): GenZonedTimeDate[F] = ev

    given syncGenZonedTimeDate[F[_]](using syncF: Sync[F]) as GenZonedTimeDate[F]:
        def make: F[ZonedDateTime] = syncF.delay(ZonedDateTime.now())

