package backend

import cats.effect.IO
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import io.finch._
import io.finch.circe._
import io.finch.catsEffect._
import com.twitter.finagle.http.filter.Cors
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

import Respond._
import Insert._
import Update._
import Delete._
import Auth._

object Main extends App {
  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def login: Endpoint[IO, Json] = post("login" :: jsonBody[Credentials]) {
    request: Credentials =>
      checkCredentials(request)
  }

  def register: Endpoint[IO, Json] = post("register" :: jsonBody[Credentials]) {
    request: Credentials =>
      registerUser(request)
  }

  def getUsers: Endpoint[IO, Json] = get("users" :: auth) {
    token: Authorization =>
      getUserList(token)
  }

  def userData: Endpoint[IO, Json] = get("users" :: path[Int] :: auth) {
    (userId: Int, token: Authorization) =>
      getUserInfo(userId, token)
  }

  def getWords: Endpoint[IO, Json] = get("dictionary" :: path[Int] :: auth) { 
    (userId: Int, token: Authorization) => 
      allUserWords(userId, token)
  }

  def trainingWords: Endpoint[IO, Json] = post("training" :: path[Int] :: jsonBody[TrainRequest] :: auth) {
    (userId: Int, request: TrainRequest, token: Authorization) =>
      trainingUserWords(userId, request, token)
  }

  def getLangWords: Endpoint[IO, Json] = post("dictionary" :: path[Int] :: jsonBody[DictRequest] :: auth) {
    (userId: Int, request: DictRequest, token: Authorization) =>
      languageWords(userId, request, token)
  }

  def allLangWords: Endpoint[IO, Json] = post("dictionary" :: jsonBody[DictRequest] :: auth) {
    (request: DictRequest, token: Authorization) =>
      allLanguageWords(request, token)
  }

  def insertWords: Endpoint[IO, Json] = post("words" :: jsonBody[InsertRequest] :: auth) {
    (request: InsertRequest, token: Authorization) =>
      insertMany(request, token)
  }

  def updateWords: Endpoint[IO, Json] = put("words" :: path[Int] :: jsonBody[UpdateWordRequest] :: auth) {
    (wordId: Int, request: UpdateWordRequest, token: Authorization) =>
      updateWordPair(wordId, request, token)
  }

  def updateScore: Endpoint[IO, Json] = put("user_words" :: jsonBody[UpdateScoreRequest] :: auth) {
    (request: UpdateScoreRequest, token: Authorization) =>
      updateUserScore(request, token)
  }

  def deleteWords: Endpoint[IO, Json] = delete("words" :: path[String] :: auth) {
    (ids: String, token: Authorization) =>
      removeUserWord(ids, token)
  }

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](
      login :+: register :+: getWords :+: getLangWords :+: allLangWords :+: getUsers :+: userData :+:
      trainingWords :+: insertWords :+: updateWords :+: updateScore :+: deleteWords
    )
    .toService
  
  val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some("*"),
    allowsMethods = _ => Some(Seq("GET", "POST", "PUT", "DELETE")),
    allowsHeaders = _ => Some(Seq("*"))
  )

  val corsService: Service[Request, Response] = new Cors.HttpFilter(policy).andThen(service)
  Await.ready(Http.server.serve(":8081", corsService))

}