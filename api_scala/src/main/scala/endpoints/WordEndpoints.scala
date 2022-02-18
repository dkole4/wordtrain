package backend

import cats.effect.IO
import io.finch._
import io.finch.circe._
import io.finch.catsEffect._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

import Retrieve._
import ResponseModels.{ UserWordPair }
import DBModels.{ Words }
import Auth.{ auth, Authorization }


object WordEndpoints {
  def getWords: Endpoint[IO, Seq[UserWordPair]] = get("dictionary" :: path[Int] :: auth) { 
    (userId: Int, token: Authorization) => 
      allUserWords(userId, token)
  }

  def trainingWords: Endpoint[IO, Seq[UserWordPair]] = post("training" :: path[Int] :: jsonBody[TrainRequest] :: auth) {
    (userId: Int, request: TrainRequest, token: Authorization) =>
      trainingUserWords(userId, request, token)
  }

  def getLangWords: Endpoint[IO, Seq[Words]] = post("dictionary" :: path[Int] :: jsonBody[DictRequest] :: auth) {
    (userId: Int, request: DictRequest, token: Authorization) =>
      languageWords(userId, request, token)
  }

  def allLangWords: Endpoint[IO, Seq[Words]] = post("dictionary" :: jsonBody[DictRequest] :: auth) {
    (request: DictRequest, token: Authorization) =>
      allLanguageWords(request, token)
  }
}