package backend

import cats.effect.IO
import io.finch._
import io.finch.circe._
import io.finch.catsEffect._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

import Auth.{ auth, Authorization }
import DBModels.{ Words }
import Delete._


object DeleteEndpoints {
  def deleteWords: Endpoint[IO, Words] = delete("words" :: path[String] :: auth) {
    (ids: String, token: Authorization) =>
      removeUserWord(ids, token)
  }
}