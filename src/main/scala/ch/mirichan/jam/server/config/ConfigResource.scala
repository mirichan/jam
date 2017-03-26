package ch.mirichan.jam.server.config

import java.io.File
import java.net.URI
import java.nio.file.{Files, Path, Paths}

import scala.util.{Failure, Success, Try}
import argonaut._

import scalaz._
import Scalaz._

case class Config(value: String)
object Config {
  val Default: Config = Config("Default config")
  implicit val codec: CodecJson[Config] = CodecJson.derive[Config]
}

trait ConfigResource {
  def read: ConfigError \/ Config
  def write(config: Config): ConfigError \/ Unit
}

trait ConfigError
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

import ConfigError._
object LocalFileConfigResource extends ConfigResource {
  private[this] lazy val maybeWinAppdata: Option[URI] = sys.env.get("APPDATA").map(appData => Paths.get(appData).toUri.resolve("slam/"))
  private[this] lazy val maybeNixAppdata: Option[URI] = sys.props.get("user.home").map(home => Paths.get(home).toUri.resolve(".slam/"))

  private[this] lazy val configRoot = ((maybeWinAppdata, maybeNixAppdata) match {
    case (Some(p), _) => p.right
    case (_, Some(p)) => p.right
    case _            => locationNotFound.left
  })

  override def read: ConfigError \/ Config = for {
    root <- configRoot
    file = new File(root.resolve("slam.json"))

    _ <- (for {
      createDirOp <- Try(Files.createDirectories(Paths.get(root)))
      createFileOp <- Try(file.createNewFile())
    } yield ()) match {
      case Failure(t) => writeError(t).left
      case Success(_) => ().right
    }

    fileContents <- Try {
      val source = scala.io.Source.fromFile(file)
      val content = source.getLines.mkString
      source.close()
      content
    } match {
      case Failure(t) => readError(t).left
      case Success(s) => s.right
    }

    config <- {
      if (fileContents.isEmpty) Config.Default.right[ConfigError]
      else \/.fromEither(Parse.decodeEither[Config](fileContents).leftMap(parseError))
    }
  } yield config

  override def write(config: Config): ConfigError \/ Unit = ().right[ConfigError]
}
