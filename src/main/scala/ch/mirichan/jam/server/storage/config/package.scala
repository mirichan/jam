package ch.mirichan.jam.server.storage

import scalaz.\/

package object config {
  sealed trait ConfigError extends StorageError
  case object ConfigError {
    case object LocationNotFound extends ConfigError
    case class WriteError(exception: Throwable) extends ConfigError
    case class ReadError(exception: Throwable) extends ConfigError
    case class ParseError(message: String) extends ConfigError

    def locationNotFound: ConfigError = LocationNotFound
    def writeError: Throwable => ConfigError = WriteError
    def readError: Throwable => ConfigError = ReadError
    def parseError: String => ConfigError = ParseError
  }

  trait ConfigResource {
    def read: StorageError \/ Config
    def write(config: Config): StorageError \/ Unit
  }
}