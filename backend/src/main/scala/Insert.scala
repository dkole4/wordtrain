package backend

import io.finch._
import io.circe._
import io.circe.syntax._
import DatabaseHandler._
import Respond._
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
    * Insert data into the connected database.
    *
    * @param table table to insert into.
    * @param columns columns to use in insertion.
    * @param rows rows to insert.
    * @return array of inserted rows.
    */
  def insert(
    table: String,
    columns: Array[String],
    rows: Array[Array[Any]]
  ): Array[Map[String, Any]] = {
    return insertData(
      table, 
      columns.mkString(", "), 
      rows.map(_.map(mapRowValue(_)).mkString(", "))
    )
  }

  /**
    * Insert new words into the connected database.
    *
    * @param request InsertRequest containing userId and words to insert.
    * @param token the token of user inserting the words.
    * 
    * Returns 201 and inserted words if they were successfully inserted,
    * 403 if user's token is invalid, 400 if request is in incorrect form.
    */
  def insertMany(request: InsertRequest, token: Authorization): Output[Json] = {
    if (!isTokenValid(token, request.userId))
      return Unauthorized(new Exception("You have no access to perform the operation."))

    val words = request.words.map(pair => {
      val same = fetchMapArray(
        "words", 
        "*", 
        "word='%s' AND translation='%s' AND lang_word='%s' AND lang_translation='%s'".format(
          pair.word, pair.transl, pair.lang_w, pair.lang_t
        )
      ) ++ fetchMapArray(
        "words", 
        "*", 
        "word='%s' AND translation='%s' AND lang_word='%s' AND lang_translation='%s'".format(
          pair.transl, pair.word, pair.lang_t, pair.lang_w
        )
      )

      if (same.length > 0) {
        same.head
      } else {
        insert(
          "words",
          Array("word", "translation", "lang_word", "lang_translation"),
          Array(
            Array[Any](pair.word, pair.transl, pair.lang_w, pair.lang_t)
          )
        ).head
      }
    })

    val added = insert(
      "user_words",
      Array("user_id", "word_id"),
      words.map(pair => Array(request.userId, pair.apply("id")))
    ).filter(a => a != null).map(a => a.apply("word_id"))

    val addedWords = words.filter(word => added.contains(word.apply("id")))

    if (addedWords == null)
      return BadRequest(new Exception("Invalid data format."))

    return Created(valueToJson(addedWords))
  }

  /**
    * Insert one word pair into the connected database.
    *
    * @param userId the id of user inserting the word pair.
    * @param pair WordPair object representing the word.
    * @return inserted word pair as a map.
    */
  def insertOne(userId: Int, pair: WordPair): Map[String, Any] = {
    val same = fetchMapArray(
      "words", 
      "*", 
      "word='%s' AND translation='%s' AND lang_word='%s' AND lang_translation='%s'".format(
        pair.word, pair.transl, pair.lang_w, pair.lang_t
      )
    ) ++ fetchMapArray(
      "words", 
      "*", 
      "word='%s' AND translation='%s' AND lang_word='%s' AND lang_translation='%s'".format(
        pair.transl, pair.word, pair.lang_t, pair.lang_w
      )
    )

    if (same.length == 0) {
      val added = insert(
        "words",
        Array("word", "translation", "lang_word", "lang_translation"),
        Array(
          Array(pair.word, pair.transl, pair.lang_w, pair.lang_t)
        )
      )

      if (added == null)
        return null

      insert(
        "user_words",
        Array("user_id", "word_id"),
        Array(Array(userId, added.head.apply("id")))
      )

      return added.head
    } else {
      insert(
        "user_words",
        Array("user_id", "word_id"),
        Array(Array(userId, same.head.apply("id")))
      )

      return same.head
    }
  }

  /**
    * Convert a value to a suitable form for insertion.
    *
    * @param value value to convert.
    * @return string representing the value.
    */
  def mapRowValue(value: Any): String = {
    value match {
      case value: Int => value.asInstanceOf[Int].toString()
      case value: String => {
        if (value.contains("'"))
          value.toString()
        else
          "'" + value.toString() + "'"
      }
      case value: Any => "'" + value.toString() + "'"
    }
  }
}