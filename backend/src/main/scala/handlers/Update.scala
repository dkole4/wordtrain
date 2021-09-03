package backend

import io.finch._
import io.circe._
import io.circe.syntax._
import com.twitter.finagle.postgres.Row
import com.twitter.finagle.postgres.generic._

import DBModels._
import ResponseModels._
import Insert.{ insertMany, insertWord, InsertRequest }
import Delete.{ removeWord, DeleteRequest }
import Auth.{ isTokenValid, Authorization }


/** Handler of update operations. */
object Update {

  /**
    * Class representing a word update request.
    *
    * @param userId the id of user updating the word.
    * @param word updated word of the word pair.
    * @param translation updated translation of the word pair.
    */
  case class UpdateWordRequest (
    userId: Int,
    pair: UserWordPair
  )

  /**
    * Class representing user score change.
    *
    * @param wordId the id of word.
    * @param score the change in score.
    * @param tries the change in tries.
    */
  case class Change (
    wordId: Int,
    score: Int,
    tries: Int
  )

  /**
    * Class representing user score change request.
    *
    * @param userId the id of user.
    * @param changes the changes in word scores.
    */
  case class UpdateScoreRequest(
    userId: Int,
    changes: Array[Change]
  )

  /**
    * Update the last seen information of a user.
    *
    * @param userId the id of user.
    */
  def updateUserActivity(userId: Int, conn: Option[DBHandler] = None) {
    val db = if (conn.isDefined) conn.get else new DBHandler()
    db.execute(
      sql"""UPDATE users SET last_seen=NOW()+interval '3 hour' WHERE id=$userId""")
    if (!conn.isDefined)
      db.endSession()
  }

  /**
    * Check whether user has added a word pair to their dictionary.
    *
    * @param wordId the id of word pair.
    * @param userId the id of user.
    * @return true if user has the word pair, false otherwise.
    */
  def checkWordPair(wordId: Int, userId: Int, conn: Option[DBHandler] = None): Boolean = {
    val db = if (conn.isDefined) conn.get else new DBHandler()
    val res = db.fetchRow(sql"""SELECT id FROM words w, user_words uw
                                WHERE id=$wordId AND user_id=$userId 
                                AND id=word_id""")
      .isDefined
    if (!conn.isDefined) db.endSession()
    res
  }

  /**
    * Update a word pair of a user.
    *
    * @param wordId the id of word pair.
    * @param request UpdateWordRequest containing the changes and usedId.
    * @param token the token of user sending the request.
    * 
    * Returns 201 status code and updated word pair if token and userId are valid, 
    * 403 status code if token or userId are invalid and, 404 status code if word
    * is not found in user's dictionary, 400 status code if request has incorrect information.
    */
  def updateWordPair(wordId: Int, request: UpdateWordRequest, token: Authorization): Output[UserWordPair] = {
    val db = new DBHandler()
    val res = {
      if (!isTokenValid(token, request.userId, Some(db))) 
        Unauthorized(new Exception("You have no access to perform the operation."))
      
      if (!checkWordPair(wordId, request.userId, Some(db)))
        NotFound(new Exception("Words not found."))
      
      updateUserActivity(request.userId, Some(db))

      val links = 
        db.fetch(sql"""SELECT * FROM user_words WHERE word_id=$wordId""".as[UserWords])
          .get

      if (links.length > 1) {
        removeWord(DeleteRequest(wordId, request.userId), Some(db))

        val added = insertWord(request.userId, UserWordPair.toWordPair(request.pair), Some(db))

        if (added.isDefined)
          Created(added.get)
        else
          BadRequest(new Exception("Invalid word data."))
      } else {
        val updated = db.fetchRow(
          sql"""UPDATE words 
                SET word=${request.pair.word}, 
                    translation=${request.pair.translation}
                WHERE id=$wordId RETURNING *""".as[Words])

        if (updated.isDefined)    
          Created(UserWordPair(updated.get, request.pair.score, request.pair.tries))
        else
          BadRequest(new Exception("Invalid word data."))
      }
    }
    db.endSession()
    res
  }

  /**
    * Update user score of a specific word pair.
    *
    * @param request UpdateScoreRequest containing userId and score changes.
    * @param token the token of user.
    * 
    * Returns 201 status code and updated word pair if userId and token are valid,
    * 403 status code if userId are token are invalid, 404 status code if word pair
    * was not found.
    */
  def updateUserScore(request: UpdateScoreRequest, token: Authorization): Output[Array[UserWords]] = {
    val db = new DBHandler()
    val res = {
      if (!isTokenValid(token, request.userId, Some(db)))
        Unauthorized(new Exception("You have no access to perform the operation."))

      updateUserActivity(request.userId, Some(db))

      val response = request.changes
        .map(change => {
          if (!checkWordPair(change.wordId, request.userId, Some(db)))
            Option.empty
          else 
            db.fetchRow(
              sql"""UPDATE user_words 
                    SET score=score+${change.score},
                        tries=tries+${change.tries}
                    WHERE word_id=${change.wordId} AND user_id=${request.userId}
                    RETURNING *""".as[UserWords])
          }
        )
        .filter(_.isDefined)
        .map(_.get)

      if (response.length < 1)
        NotFound(new Exception("Words not found."))

      Created(response)
    }
    db.endSession()
    res
  }
}