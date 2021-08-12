package backend

import io.finch._
import io.circe._
import io.circe.syntax._
import DatabaseHandler.{ updateData, fetchMapRow, fetchMapArray }
import Respond.{ valueToJson, WordPair }
import Insert.{ insertMany, insertOne, InsertRequest }
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
    word: String,
    translation: String
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
  def updateUserActivity(userId: Int) {
    updateData("users", "last_seen=NOW() + interval '3 hour'", "id=%d".format(userId))
  }

  /**
    * Check whether user has added a word pair to their dictionary.
    *
    * @param wordId the id of word pair.
    * @param userId the id of user.
    * @return true if user has the word pair, false otherwise.
    */
  def checkWordPair(wordId: Int, userId: Int): Boolean = {
    val check = fetchMapRow(
      "words w, user_words uw", 
      "user_id, id", 
      "w.id=uw.word_id AND w.id=%d AND uw.user_id=%d".format(
        wordId, userId)
    )

    return check != null
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
  def updateWordPair(wordId: Int, request: UpdateWordRequest, token: Authorization): Output[Json] = {
    if (!isTokenValid(token, request.userId))
      return Unauthorized(new Exception("You have no access to perform the operation."))
    
    if (!checkWordPair(wordId, request.userId))
      return NotFound(new Exception("Words not found."))
    
    updateUserActivity(request.userId)

    val same = fetchMapArray("user_words", "*", "word_id=%d".format(wordId))

    if (same.length > 1) {
      val word = fetchMapRow("words", "lang_word, lang_translation", "id=%d".format(wordId))

      removeWord(DeleteRequest(wordId, request.userId))

      val added = insertOne(
        request.userId,
        WordPair(
          request.word, 
          request.translation, 
          word.apply("lang_word").toString, 
          word.apply("lang_translation").toString
        )
      )

      if (added != null)
        return Created(
          valueToJson(added)
        )
      
      return BadRequest(new Exception("Invalid word data."))
    }

    return Created(
      valueToJson(
        updateData(
          "words", 
          "word='%s', translation='%s'".format(request.word, request.translation),
          "id=%d".format(wordId)
        )
      )
    )
  }

  /**
    * Convert value to suitable form for insertion into an update query.
    *
    * @param value value to covert.
    * @return value as a string in a correct form.
    */
  def getChangeString(value: Int): String = {
    if (value >= 0) 
      return "+%d".format(value)
    return value.toString()
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
  def updateUserScore(request: UpdateScoreRequest, token: Authorization): Output[Json] = {
    if (!isTokenValid(token, request.userId))
      return Unauthorized(new Exception("You have no access to perform the operation."))

    updateUserActivity(request.userId)

    val response = valueToJson(
      request.changes.map(change => {
        if (!checkWordPair(change.wordId, request.userId)) {
          null
        } else {
          updateData(
            "user_words",
            "score=score%s, tries=tries%s".format(
              getChangeString(change.score), getChangeString(change.tries)
            ),
            "word_id=%d AND user_id=%d".format(change.wordId, request.userId)
          )
        }
      }).filter(change => change != null)
    )

    if (response == null)
      return NotFound(new Exception("Words not found."))

    return Created(response)
  }
}