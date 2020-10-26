package delivery

import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.server.staticcontent._

object AssetService:
    def service[F[_]: Sync](blocker: Blocker)(using cs: ContextShift[F]): HttpRoutes[F] =
      fileService(FileService.Config("./assets", blocker))