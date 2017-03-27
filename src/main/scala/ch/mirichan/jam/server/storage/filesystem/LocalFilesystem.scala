package ch.mirichan.jam.server.storage
package filesystem

import java.net.URI
import java.nio.file.Paths

import scalaz._
import Scalaz._

import FilesystemError._
object LocalFilesystem extends Filesystem {
  private[this] lazy val maybeWinAppdata: Option[URI] = sys.env.get("APPDATA").map(appData => Paths.get(appData).toUri.resolve("jam/"))
  private[this] lazy val maybeNixAppdata: Option[URI] = sys.props.get("user.home").map(home => Paths.get(home).toUri.resolve(".jam/"))

  override def baseLocation: StorageError \/ URI = (maybeWinAppdata, maybeNixAppdata) match {
    case (Some(p), _) => p.right
    case (_, Some(p)) => p.right
    case _            => locationNotFound.left
  }
}
