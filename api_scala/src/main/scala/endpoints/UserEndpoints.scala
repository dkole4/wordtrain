package backend

import cats.effect.IO
import io.finch._
import io.finch.circe._
import io.finch.catsEffect._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

import Auth.{ auth, Authorization }
import ResponseModels.{ UserInfo, UserInfoExtended }
import Retrieve._

object UserEndpoints {
  def getUsers: Endpoint[IO, Seq[UserInfo]] = get("users" :: auth) {
    token: Authorization =>
      getUserList(token)
  }

  def userData: Endpoint[IO, UserInfoExtended] = get("users" :: path[Int] :: auth) {
    (userId: Int, token: Authorization) =>
      getUserInfo(userId, token)
  }
}