package backend

import io.finch._
import io.circe._
import io.circe.syntax._
import com.twitter.finagle.postgres._
import com.twitter.finagle.postgres.generic._

import DBModels._
import DBHandler._
import Auth.{ isTokenValid, Authorization }


/** Handler of delete operations.  */
object Delete {

  /**
    * Class representing a word removal request.
    *
    * @param wordId the id of the word to remove.
    * @param userId the id of the user sending the request.
    */
  case class DeleteRequest (
    wordId: Int,
    userId: Int
  )

  /**
    * Remove a user's word from database.
    *
    * @param ids word and user ids in form wordId:userId
    * @param token the token of user
    * 
    * Returns 204 status code if operation was successful, 404 status code 
    * if no matching word was found, 403 status code if user's token is 
    * invalid, 400 status if request data is invalid or corrupted.
    */
  def removeUserWord(ids: String, token: Authorization): Output[Words] = {
    try {
      val data = ids.split(":").map(_.toInt)

      if (data.length != 2) {
        NotFound(new Exception("Word not found"))
      } else {
        if (!isTokenValid(token, data(0)))
          Unauthorized(new Exception("You don't have access to perform the operation."))

        if (removeWord( DeleteRequest(data(0), data(1)) ))
          NoContent
        else
          NotFound(new Exception("Word not found"))
      }
    } catch {
      case _: Throwable =>
        BadRequest(new Exception("Entered ids are not integers"))
    }
  }
  
  /**
    * Remove a user's word from database. 
    * Besides the link also removes the word if 
    * no other user is using the word.
    *
    * @param request request object containing the word and user ids
    * @return 1 if operation was successful, 0 otherwise
    */
  def removeWord(request: DeleteRequest): Boolean = {
    val links =
      fetch(sql"SELECT user_id FROM user_words WHERE word_id=${request.wordId}")
        .get

    val link = execute(
      sql"""DELETE FROM user_words 
            WHERE word_id=${request.wordId}
            AND user_id=${request.userId}""")

    if (links.length == 1 && links.head.get[Int]("user_id") == request.userId)
      execute(sql"DELETE FROM words WHERE id=${request.wordId}")
    else
      link
  }
}