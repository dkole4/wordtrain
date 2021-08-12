package backend

import io.finch._
import io.circe._
import io.circe.syntax._

import DatabaseHandler._
import Update.{ updateUserActivity }
import Auth.{ isTokenValid }
import Auth.Authorization


/** Handler of fetching operations. */
object Respond {

  /**
    * Class representing a request of specific language wordset.
    *
    * @param lang_w the language of words.
    * @param lang_t the language of translations.
    */
  case class DictRequest(
    lang_w: String,
    lang_t: String
  )

  /**
    * Class representing a training session request.
    *
    * @param lang_w the language of words.
    * @param lang_t the language of translations.
    * @param size the number of words.
    */
  case class TrainRequest(
    lang_w: String,
    lang_t: String,
    size: Int
  )

  /**
    * Class representing a word pair.
    *
    * @param word the word of word pair.
    * @param transl the translation of word pair.
    * @param lang_w the language of word.
    * @param lang_t the language of translation.
    */
  case class WordPair (
    word: String, 
    transl: String, 
    lang_w: String, 
    lang_t: String
  )

  /**
    * Fetch all the words added by a user.
    *
    * @param userId the id of user sending the request.
    * @param token the token of user.
    * 
    * Returns 200 status code and words if token and userId are
    * valid, 403 status code otherwise.
    */
  def allUserWords(userId: Int, token: Authorization): Output[Json] = {
    if (!isTokenValid(token, userId))
      return Unauthorized(new Exception("You don't have access to this information."))
    
    updateUserActivity(userId)

    return Ok(
      valueToJson(
       fetchMapArray(
          "words AS w, user_words AS uw",
          "*",
          f"uw.user_id=$userId%d AND w.id=uw.word_id"
        )
      )
    )
  }

  /**
    * Fetch user words for a training session.
    *
    * @param userId the id of user.
    * @param request TrainRequest containing languages and size of training wordset.
    * @param token the token of user.
    * 
    * Returns 200 status code and training words if token and userId are valid, 
    * 403 status code otherwise.
    */
  def trainingUserWords(userId: Int, request: TrainRequest, token: Authorization): Output[Json] = {
    if (!isTokenValid(token, userId))
      return Unauthorized(new Exception("You don't have access to this information."))

    return Ok(
      valueToJson(
        getWords(
          userId,
          DictRequest(request.lang_w, request.lang_t),
          " ORDER BY score / (tries + 1) ASC LIMIT %d".format(request.size)
        )
      )
    )
  }

  /**
    * Fetch all the available words of a specific language set.
    *
    * @param request DictRequest containing the languages.
    * @param token the token of user sending the request.
    * 
    * Returns 200 status code and words if user's token is valid,
    * 403 status code otherwise.
    */
  def allLanguageWords(request: DictRequest, token: Authorization): Output[Json] = {
    if (!isTokenValid(token))
      return Unauthorized(new Exception("You don't have access to this information."))

    val words = fetchMapArray(
      "words AS w",
      "*",
      "lang_word='%s' AND lang_translation='%s'".format(
        request.lang_w.toLowerCase(), request.lang_t.toLowerCase())
    ) ++ fetchMapArray(
      "words AS w",
      "id, word AS translation, translation AS word, lang_word AS lang_translation, lang_translation AS lang_word",
      "lang_word='%s' AND lang_translation='%s'".format(
        request.lang_t.toLowerCase(), request.lang_w.toLowerCase())
    )

    return Ok(
      valueToJson(words)
    )
  }

  /**
    * Fetch all the words of a specific language set added by a user.
    *
    * @param userId the id of user sending the request.
    * @param request DictRequest containing the languages.
    * @param token the token of user.
    * 
    * Returns 200 status code and words if token and userId are valid,
    * 403 status code otherwise.
    */
  def languageWords(userId: Int, request: DictRequest, token: Authorization): Output[Json] = {
    if (!isTokenValid(token, userId))
      return Unauthorized(new Exception("You don't have access to this information."))

    return Ok(
      valueToJson(
        getWords(userId, request)
      )
    )
  }

  /**
    * Fetch user's added words.
    *
    * @param userId the id of user.
    * @param request DictRequest containing the languages to use in fetching.
    * @param condition additional WHERE conditions to use in fetching.
    * @return array of fetched words as maps.
    */
  def getWords(
    userId: Int, 
    request: DictRequest, 
    condition: String = ""
  ): Array[Map[String, Any]] = {
    updateUserActivity(userId)

    val words = fetchMapArray(
      "words AS w, user_words AS uw",
      "*",
      "uw.user_id=%d AND w.id=uw.word_id AND lang_word='%s' AND lang_translation='%s'%s".format(
        userId, request.lang_w.toLowerCase(), request.lang_t.toLowerCase(), condition)
    ) ++ fetchMapArray(
      "words AS w, user_words AS uw",
      "id, word AS translation, translation AS word, lang_word AS lang_translation, lang_translation AS lang_word, uw.*",
      "uw.user_id=%d AND w.id=uw.word_id AND lang_word='%s' AND lang_translation='%s'%s".format(
        userId, request.lang_t.toLowerCase(), request.lang_w.toLowerCase(), condition)
    )

    return words
  }

  /**
    * Fetch information about all the registered users.
    *
    * @param token the token of user sending the request.
    * 
    * Returns 200 status code and user information if token is valid,
    * 403 status code otherwise.
    */
  def getUserList(token: Authorization): Output[Json] = {
    if (!isTokenValid(token))
      return Unauthorized(new Exception("Invalid credentials"))

    return Ok(
      valueToJson(
        fetchMapArray(
          "users u",
          "u.id, username, sum(score) AS score, sum(tries) AS tries, last_seen, count(id) AS word_count",
          null,
          "user_words uw ON u.id=uw.user_id GROUP BY id"
        )
      )
    )
  }

  /**
    * Fetch user's information.
    *
    * @param userId the id of user to fetch information about.
    * @param token the token of user sending the request.
    * 
    * Returns 200 status code and user information if token is valid,
    * 403 status code if token is invalid, 404 status code if
    * searched user is not found.
    */
  def getUserInfo(userId: Int, token: Authorization): Output[Json] = {
    if (!isTokenValid(token))
      return Unauthorized(new Exception("Invalid credentials."))

    val user = fetchMapRow(
      "users u",
      "u.id, username, sum(score) AS score, sum(tries) AS tries, last_seen",
      "u.id=%d GROUP BY u.id".format(userId),
      "user_words uw ON u.id=uw.user_id"
    )

    if (user == null)
      return NotFound(new Exception("User not found."))

    val words = fetchMapArray(
      "user_words uw, words w",
      "w.id, word, translation, lang_word, lang_translation",
      "w.id=word_id AND user_id=%d".format(userId)
    )

    if (words == null)
      return Ok(
        valueToJson(user ++ Map("words" -> Array(), "word_count" -> 0))
      )
    
    return Ok(
      valueToJson(user ++ Map("words" -> words, "word_count" -> words.length))
    )
  }

  /**
    * Convert map to Json.
    *
    * @param map map to convert.
    * @return given map as a Json object.
    */
  def mapToJson(map: Map[_, _]): Json = {
    return map.map({ case (k, v) => k.toString() -> valueToJson(v)}).asJson
  }

  /**
    * Convert array to Json.
    *
    * @param array array to convert.
    * @return given array as a Json object.
    */
  def arrayToJson(array: Array[Any]): Json = {
    return array.map(v => valueToJson(v)).asJson
  }

  /**
    * Convert a value to Json.
    *
    * @param value value to convert.
    * @return given value as a Json object.
    */
  def valueToJson(value: Any): Json = {
    value match {
      case value: Int => Json.fromInt(value)
      case value: String => Json.fromString(value)
      case value: Array[Any] => arrayToJson(value)
      case value: Map[_, _] => mapToJson(value)
      case value: Json => value
      case value: Any => Json.fromString(value.toString())
      case null => null
    }
  }
}