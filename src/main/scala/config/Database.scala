package config

import pureconfig._
import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

final case class DatabaseConfig(
    driver: String, 
    url: String, 
    user: String, 
    pass: String, 
    poolSize: Int
)

object DatabaseConfig:
    given configReader as ConfigReader[DatabaseConfig] = ConfigReader.forProduct5("driver", "url", "user", "pass", "poolSize")(DatabaseConfig(_, _, _, _, _))

    def dbTransactor[F[_]: Async: ContextShift](config: DatabaseConfig, connEc: ExecutionContext, blocker: Blocker): Resource[F, HikariTransactor[F]] =
        HikariTransactor
            .newHikariTransactor[F](config.driver, config.url, config.user, config.pass, connEc, blocker)