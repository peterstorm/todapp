package config

import pureconfig._

final case class ServerConfig(
    host: String,
    port: Int 
)

object ServerConfig:
    given configReader as ConfigReader[ServerConfig] = ConfigReader.forProduct2("host", "port")(ServerConfig(_, _))