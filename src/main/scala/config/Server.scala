package config

import pureconfig._

final case class ServerConfig(
    host: String,
    port: Int 
)

object ServerConfig:
    given ConfigReader[ServerConfig] = ConfigReader.forProduct2("host", "port")(ServerConfig(_, _))
