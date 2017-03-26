package ch.mirichan.jam.server

import argonaut._

import org.http4s._
import org.http4s.server._
import org.http4s.argonaut._
import org.http4s.dsl._

object RestApi {
  object Routes {
    val track: /   = Root / "track"
    val game: /    = Root / "game"
    val headers: / = Root / "headers"
  }

  import Routes._
  val service = HttpService {
    case GET -> `track` / trackId =>
      Ok(Json.obj("track" -> Json.jString(trackId)))
    case GET -> `game` / gameId =>
      Ok(Json.obj("game" -> Json.jString(gameId)))
    case r @ GET -> `headers` =>
      Ok(Json.obj("headers" -> Json.jArray(r.headers.map(h => Json.obj(h.name.value -> Json.jString(h.value))).toList)))
  }
}
