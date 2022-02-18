package backend

import cats.effect.IO
import io.finch._
import io.finch.circe._
import io.finch.catsEffect._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

import Auth.{ auth, Authorization }
import ResponseModels.{ UserWordPair }
import DBModels.{ UserWords }
import Update._


object UpdateEndpoints {
  def updateWords: Endpoint[IO, UserWordPair] = put("words" :: path[Int] :: jsonBody[UpdateWordRequest] :: auth) {
    (wordId: Int, request: UpdateWordRequest, token: Authorization) =>
      updateWordPair(wordId, request, token)
  }

  def updateScore: Endpoint[IO, Array[UserWords]] = put("user_words" :: jsonBody[UpdateScoreRequest] :: auth) {
    (request: UpdateScoreRequest, token: Authorization) =>
      updateUserScore(request, token)
  }
}