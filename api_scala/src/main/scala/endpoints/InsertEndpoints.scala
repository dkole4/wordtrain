package backend

import cats.effect.IO
import io.finch._
import io.finch.circe._
import io.finch.catsEffect._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

import Auth.{ auth, Authorization }
import ResponseModels.UserWordPair
import Insert._


object InsertEndpoints {
  def insertWords: Endpoint[IO, Array[UserWordPair]] = post("words" :: jsonBody[InsertRequest] :: auth) {
    (request: InsertRequest, token: Authorization) =>
      insertMany(request, token)
  }
}