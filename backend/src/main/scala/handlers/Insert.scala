package backend

import io.finch._
import io.circe._
import io.circe.syntax._
import com.twitter.finagle.postgres.Row
import com.twitter.finagle.postgres.generic._

import DBModels._
import ResponseModels._
import Retrieve._
import Auth.{ isTokenValid, Authorization }


/** Handler of insert operations. */
object Insert {
  
  /**
    * Class representing a word insertion request.
    *
    * @param userId the id of user adding the word.
    * @param words array of WordPair objects representing words.
    */
  case class InsertRequest(userId: Int, words: Array[WordPair])

  /**
    * Insert new words into the connected database.
    *
    * @param request InsertRequest containing userId and words to insert.
    * @param token the token of user inserting the words.
    * 
    * Returns 201 and inserted words if they were successfully inserted,
    * 403 if user's token is invalid, 400 if request is in incorrect form.
    */
  def insertMany(request: InsertRequest, token: Authorization): Output[Array[UserWordPair]] = {
    val db = new DBHandler()
    val res = {
      if (!isTokenValid(token, request.userId, Some(db)))
        Unauthorized(new Exception("You have no access to perform the operation."))

      val words = request.words
        .map(insertWord(request.userId, _, Some(db)))
        .filter(_.isDefined)
        .map(_.get)

      if (words.length < 1)
        BadRequest(new Exception("Invalid data format."))
      else
        Created(words) 
    }
    db.endSession()
    res
  }

  /**
    * Insert one word pair into the connected database.
    *
    * @param userId the id of user inserting the word pair.
    * @param pair WordPair object representing the word.
    * @return inserted word pair as a map.
    */
  def insertWord(
    userId: Int, pair: WordPair, conn: Option[DBHandler] = None
  ): Option[UserWordPair] = {
    val db = if (conn.isDefined) conn.get else new DBHandler()
    val same = db.fetchRow(
      sql"""SELECT * FROM words
            WHERE (word=${pair.word} AND translation=${pair.translation}
                    AND lang_word=${pair.lang_word}
                    AND lang_translation=${pair.lang_translation}) OR
                  (translation=${pair.word} AND word=${pair.translation}
                    AND lang_word=${pair.lang_translation}
                    AND lang_translation=${pair.lang_word})""".as[Words])

    val res: Option[UserWordPair] = if (!same.isDefined) {
      val added = db.fetchRow(
        sql"""INSERT INTO words(word, translation, lang_word, lang_translation)
              VALUES (${pair.word}, ${pair.translation}, 
                      ${pair.lang_word}, ${pair.lang_translation})
              RETURNING *""".as[Words])
      
      if (!added.isDefined) 
        Option.empty
      else {
        db.execute(sql"""INSERT INTO user_words(user_id, word_id)
                        VALUES ($userId, ${added.get.id})""")

        Option( UserWordPair(added.get) )
      }
    } else {
      db.execute(sql"""INSERT INTO user_words(user_id, word_id)
                       VALUES ($userId, ${same.get.id})""")

      Option( UserWordPair(same.get) )
    }

    if (!conn.isDefined)
      db.endSession()
    res
  }
}