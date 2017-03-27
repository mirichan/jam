package ch.mirichan.jam.server.storage

import java.net.URI

import scalaz.\/

package object filesystem {
  sealed trait FilesystemError extends StorageError

  object FilesystemError {
    object LocationNotFound extends FilesystemError
    def locationNotFound: FilesystemError = LocationNotFound
  }

  trait Filesystem {
    def baseLocation: StorageError \/ URI
  }
}