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

import AuthEndpoints._
import WordEndpoints._
import UserEndpoints._
import InsertEndpoints._
import DeleteEndpoints._
import UpdateEndpoints._

object Main extends App {
  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](
      login :+: register :+: changePassword :+: getWords :+: getLangWords :+: 
      allLangWords :+: getUsers :+: userData :+:trainingWords :+: insertWords :+:
      updateWords :+: updateScore :+: deleteWords
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