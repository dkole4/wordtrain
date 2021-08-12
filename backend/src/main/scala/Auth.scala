package backend

import cats.effect.IO
import io.finch.catsEffect._
import io.finch._
import io.circe._
import io.circe.syntax._
import java.security.MessageDigest
import java.security.SecureRandom
import com.github.t3hnar.bcrypt._

import DatabaseHandler._
import Respond.{ valueToJson }
import Insert.{ insert }
import Update.{ updateUserActivity }
import scala.util.Success
import scala.util.Failure


/** Authentication and authorization handler. */
object Auth {

  /**
    * Class representing the Authorization header.
    *
    * @param token token passed in the header.
    */
  case class Authorization(token: String)

  /**
    * Class representing the JSON body of a login request.
    *
    * @param username the name of a user.
    * @param password the password of a user.
    */
  case class Credentials(username: String, password: String)
  
  /** Endpoint that maps Authorization header into correspoding class. */
  val auth: Endpoint[IO, Authorization] = header("Authorization").mapOutput(u =>
    Ok(Authorization(u))
  )

  /**
    * Validate the password of a user.
    *
    * @param password hashed password of a user.
    * @param received received password from a login attempt.
    * @return true if passwords are matching, false otherwise.
    */
  def validate(password: String, received: String): Boolean = {
    received.isBcryptedSafe(password) match {
      case Success(result) => result
      case Failure(failure) => false
    }
  }

  /**
    * Check the credentials received in a login attempt.
    *
    * @param request Credentials object containing 
    * the name and password of the user.
    * 
    * Returns 200 status code with user data and token 
    * if credentials are valid, 403 status code otherwise.
    */
  def checkCredentials(request: Credentials): Output[Json] = {
    val user = fetchMapRow(
      "users", 
      "*", 
      "username='%s'".format(request.username)
    )

    if (user == null)
      return Forbidden(new Exception("No user with such credentials found"))
    
    if (!validate(user.apply("passwd").toString, request.password))
      return Forbidden(new Exception("Invalid password"))
    
    val userId = user.apply("id").asInstanceOf[Int]
    updateUserActivity(userId)

    return Ok(
      valueToJson(user ++ Map("token" -> generateToken(userId)))
    )
  }

  /**
    * Generate a temporary token for a user and
    * insert it into connected database.
    *
    * @param userId the id of user to generate token for. 
    * @return the generated token.
    */
  def generateToken(userId: Int): String = {
    val generator = new TokenGenerator()
    val token = generator.generateToken()

    val existing = fetchMapRow("token", "token", "user_id=%d".format(userId))

    if (existing == null) {
      val row = insert(
        "token",
        Array("user_id", "token", "expires"),
        Array(Array(userId, token, "NOW() + interval '1 day'"))
      ).head

      return row.apply("token").toString
    } 

    val updated = updateData(
      "token", "token='%s', expires=NOW() + interval '1 day'".format(token), "user_id=%d".format(userId)
    )

    return updated.apply("token").toString
  }

  /**
    * Check if given token is valid.
    *
    * @param header Authorization object containing the token.
    * @param userId id the user that sent the token (optional).
    * @return true if token is valid, false otherwise.
    */
  def isTokenValid(header: Authorization, userId: Int = -1): Boolean = {
    val headerParts = header.token.split(" ")
    if (headerParts.length != 2 || headerParts(0) != "bearer")
      return false

    val token = headerParts(1)
    val tokenCondition = if (userId <= 0)
      "token='%s'".format(token) 
    else 
      "token='%s' AND user_id=%d".format(token, userId)

    val result = fetchMapRow("token", "*", tokenCondition)
    return result != null
  }

  /**
    * Register the user using given credentials.
    *
    * @param request Credentials object containing 
    * the username and password of a new user.
    * 
    * Returns 200 status code with inserted user data 
    * and token, 400 status code otherwise.
    */
  def registerUser(request: Credentials): Output[Json] = {
    val existing = fetchMapRow(
      "users", "username", "username='%s'".format(request.username)
    )

    if (existing != null)
      return BadRequest(new Exception("Username is already taken"))
    
    val user = insert(
      "users", 
      Array("username", "passwd"), 
      Array(Array(request.username, request.password))
    )
    .head
    .-("passwd")

    return Ok(
      valueToJson(
        user ++ Map(
          "token" -> generateToken(user.apply("id").asInstanceOf[Int])
        )
      )
    )
  }
}

/** Class used for token generation. */
class TokenGenerator {
  
  val TOKEN_LENGTH = 64

  val TOKEN_CHARS = "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm_.-"
  val secureRandom = new SecureRandom()

  /**
    * Generate a new token.
    *
    * @return new token as a string.
    */
  def generateToken(): String = {
    val charsLength = TOKEN_CHARS.length()

    def tokenConstructor(token: String, charsLeft: Int): String = {
      if (charsLeft == 0) return token
      else return tokenConstructor(
        token + TOKEN_CHARS.charAt(secureRandom.nextInt(charsLength)).toString, charsLeft-1
      )
    }

    return tokenConstructor("", TOKEN_LENGTH)
  }
}