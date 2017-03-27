package ch.mirichan.jam.server.storage.config

import argonaut.CodecJson

case class Config(value: String)
object Config {
  val Default: Config = Config("Default config")
  implicit val codec: CodecJson[Config] = CodecJson.derive[Config]
}
