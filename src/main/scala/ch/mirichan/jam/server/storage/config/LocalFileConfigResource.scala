package ch.mirichan.jam.server
package storage.config

import java.io.File
import java.nio.file.{Files, Paths}

import scala.util.{Failure, Success, Try}

import ch.mirichan.jam.server.storage.StorageError
import ch.mirichan.jam.server.storage.filesystem.Filesystem

import argonaut._

import scalaz._
import Scalaz._

import ConfigError._
final class LocalFileConfigResource(fs: Filesystem) extends ConfigResource {
  override def read: StorageError \/ Config = for {
    root <- fs.baseLocation
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

object LocalFileConfigResource {
  def apply(storage: Filesystem): LocalFileConfigResource = new LocalFileConfigResource(storage)
}