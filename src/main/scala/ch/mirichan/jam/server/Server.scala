package ch.mirichan.jam.server

import java.util.concurrent.{ExecutorService, Executors}

import ch.mirichan.jam.server.config.LocalFileConfigResource

import scala.util.Properties.envOrNone
import scalaz.concurrent.Task
import org.http4s.server.{Server, ServerApp}
import org.http4s.server.blaze.BlazeBuilder


object BlazeExample extends ServerApp {

  val port : Int              = envOrNone("HTTP_PORT") map (_.toInt) getOrElse 8080
  val host : String           = "0.0.0.0"
  val pool : ExecutorService  = Executors.newCachedThreadPool()

  override def server(args: List[String]): Task[Server] = {
    val config = LocalFileConfigResource.read
    BlazeBuilder
      .bindHttp(port, host)
      .mountService(RestApi.service)
      .withServiceExecutor(pool)
      .start
  }
}
