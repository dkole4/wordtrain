package backend

import cats.effect.IO
import io.finch._
import io.finch.circe._
import io.finch.catsEffect._
import com.twitter.finagle.http.filter.Cors
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

import Auth._
import DBModels._
import ResponseModels._

object AuthEndpoints {
  def login: Endpoint[IO, TokenizedUser] = post("login" :: jsonBody[Credentials]) {
    request: Credentials =>
      checkCredentials(request)
  }

  def register: Endpoint[IO, TokenizedUser] = post("register" :: jsonBody[Credentials]) {
    request: Credentials =>
      registerUser(request)
  }

  def changePassword: Endpoint[IO, Boolean] = post("password" :: jsonBody[PasswordUpdate] :: auth) {
    (request: PasswordUpdate, token: Authorization) =>
      changeUserPassword(request, token)
  }
}