package config

import cats.effect._
import cats.implicits._
import pureconfig._
import pureconfig.module.catseffect.syntax._
import com.typesafe.config.ConfigFactory

final case class Config(
    server: ServerConfig, 
    database: DatabaseConfig
)

object Config:
    // change back to the other version when pureconfig is fixed with dotty compat. This is a bit of a hack!
    // given configReader as ConfigReader[Config] = ConfigReader.forProduct2("server", "database")(Config(_, _)) 
    given configReader as Derivation[ConfigReader[Config]] = Derivation.Successful(ConfigReader.forProduct2("server", "database")(Config(_, _)))

    def load[F[_]: Sync: ContextShift](configFile: String = "application.conf"): Resource[F, Config] =
        Blocker[F].flatMap { blocker => 
            Resource.liftF(ConfigSource.fromConfig(ConfigFactory.load(configFile)).loadF[F, Config](blocker))
        }