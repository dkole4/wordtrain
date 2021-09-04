package backend

import cats.effect.IO
import io.finch.catsEffect._
import io.finch._
import java.security.MessageDigest
import java.security.SecureRandom
import com.github.t3hnar.bcrypt._
import com.twitter.finagle.postgres._
import com.twitter.finagle.postgres.generic._

import DBModels._
import DBHandler._
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

  case class TokenizedUser(id: Int, username: String, token: String)
  /**
    * Class representing the JSON body of a login request.
    *
    * @param username the name of a user.
    * @param password the password of a user.
    */
  case class Credentials(username: String, password: String)
  
  case class PasswordUpdate(
    userId: Int, newPassword: String, oldPassword: String)

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
  def checkCredentials(request: Credentials): Output[TokenizedUser] = {
    val user = 
      fetchRow(sql"SELECT * FROM users WHERE username=${request.username}".as[Users])
      
    if (!user.isDefined)
      Forbidden(new Exception("No user with such credentials found"))
    else if (!validate(user.get.passwd, request.password))
      Forbidden(new Exception("Invalid password"))
    else {
      updateUserActivity(user.get.id)
      Ok(TokenizedUser(user.get.id, user.get.username, generateToken(user.get.id)))
    }
  }

  def changeUserPassword(
    request: PasswordUpdate, token: Authorization
  ): Output[Boolean] = {
    val user = 
      fetchRow(sql"SELECT * FROM users WHERE user_id=${request.userId}".as[Users])

    if (!user.isDefined)
      Forbidden(new Exception("No user with such credentials found"))
    else if (!isTokenValid(token, request.userId))
      Forbidden(new Exception("Invalid token"))
    else if (!validate(user.get.passwd, request.oldPassword))
      Forbidden(new Exception("Invalid password"))
    else {
      updateUserActivity(user.get.id)
      
      val hash = request.newPassword.bcryptSafeBounded
      hash match {
        case Success(h) =>
          Ok(
            execute(
              sql"UPDATE users SET password=${hash.get} WHERE user_id=${request.userId}")
          )
        case Failure(e) =>
          InternalServerError(new Exception("Password change wasn't successful, try again."))
      }
    }
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

    val existing =
      fetchRow(sql"SELECT token FROM token WHERE user_id=${userId}")

    if (!existing.isDefined) {
      fetchRow(sql"""INSERT INTO token(user_id, token, expires)
                     VALUES ($userId, $token, NOW() + interval '1 day') RETURNING token""")
        .get
        .get[String]("token")
    } 
    else {
      fetchRow(sql"""UPDATE token SET token=$token, expires=NOW() + interval '1 day' 
                     WHERE user_id=$userId RETURNING token""")
        .get
        .get[String]("token")
    }
  }

  /**
    * Check if given token is valid.
    *
    * @param header Authorization object containing the token.
    * @param userId id the user that sent the token (optional).
    * @return true if token is valid, false otherwise.
    */
  def isTokenValid(
    header: Authorization, userId: Int = -1
  ): Boolean = {
    val headerParts = header.token.split(" ")
    if (headerParts.length != 2 || headerParts(0) != "bearer") false

    val token = headerParts(1)
    
    if (userId <= 0)
      fetchRow(sql"SELECT * FROM token WHERE token=$token")
        .isDefined
    else 
      fetchRow(sql"SELECT * FROM token WHERE token=$token AND user_id=$userId")
        .isDefined
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
  def registerUser(request: Credentials): Output[TokenizedUser] = {
    val existing =
      fetchRow(sql"SELECT username FROM users WHERE username=${request.username}")

    if (existing.isDefined) 
      BadRequest(new Exception("Username is already taken"))
    else {
      val hash = request.password.bcryptSafeBounded
      hash match {
        case Success(h) =>
          val user = 
            fetchRow(sql"""INSERT INTO users(username, passwd) 
                                VALUES (${request.username}, ${hash.get}) RETURNING id, username""")
              .get
          
          Ok(
            TokenizedUser(
              user.get[Int]("id"), 
              user.get[String]("username"),
              generateToken(user.get[Int]("id"))
            )
          )
        case Failure(e) =>
          InternalServerError(new Exception("Registration wasn't successful, try again."))
      }
    }
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