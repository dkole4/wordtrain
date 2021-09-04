package backend

import io.finch._
import io.circe._
import io.circe.syntax._
import com.twitter.finagle.postgres.generic._

import DBModels._
import DBHandler._
import ResponseModels._
import Update.{ updateUserActivity }
import Auth.{ isTokenValid }
import Auth.Authorization


/** Handler of fetching operations. */
object Retrieve {

  /**
    * Class representing a request of specific language wordset.
    *
    * @param lang_w the language of words.
    * @param lang_t the language of translations.
    */
  case class DictRequest(
    lang_word:        String,
    lang_translation: String
  )

  /**
    * Class representing a training session request.
    *
    * @param lang_w the language of words.
    * @param lang_t the language of translations.
    * @param size the number of words.
    */
  case class TrainRequest(
    lang_word:        String,
    lang_translation: String,
    size:             Int
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
  def allUserWords(userId: Int, token: Authorization): Output[Seq[UserWordPair]] = {
    if (!isTokenValid(token, userId))
      Unauthorized(new Exception("You don't have access to this information."))
    else {
      updateUserActivity(userId)

      val res = fetch(
        sql"""SELECT w.*, score, tries 
              FROM words w, user_words uw
              WHERE user_id=$userId AND word_id=id""".as[UserWordPair])

      if (res.isDefined)
        Ok(res.get)
      else
        InternalServerError(new Exception("Server error occurred, try again."))
    }
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
  def trainingUserWords(
    userId: Int, request: TrainRequest, token: Authorization
  ): Output[Seq[UserWordPair]] = {
    if (!isTokenValid(token, userId)) 
      Unauthorized(new Exception("You don't have access to this information."))
    else {
      val res = fetch(
        sql"""
           SELECT * FROM 
           ((SELECT w.*, score, tries FROM words w, user_words uw
             WHERE user_id=$userId AND id=word_id 
                 AND lang_word=${request.lang_word}
                 AND lang_translation=${request.lang_translation})
             UNION (SELECT id, translation, word, lang_translation, 
                           lang_word, score, tries
                    FROM words w, user_words uw
                    WHERE user_id=$userId AND id=word_id
                       AND lang_word=${request.lang_translation}
                       AND lang_translation=${request.lang_word})) AS words
            ORDER BY score / (tries + 1) ASC LIMIT ${request.size}""".as[UserWordPair])
      
      if (res.isDefined)
        Ok(res.get)
      else
        InternalServerError(new Exception("Server error occurred, try again."))
    }
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
  def allLanguageWords(
    request: DictRequest, token: Authorization
  ): Output[Seq[Words]] = {
    if (!isTokenValid(token))
      Unauthorized(new Exception("You don't have access to this information."))
    else {
      val res = fetch(
        sql"""SELECT * FROM words
              WHERE lang_word=${request.lang_word}
                  AND lang_translation=${request.lang_translation}
              UNION (SELECT id, translation, word, lang_translation, lang_word
                      FROM words
                      WHERE lang_word=${request.lang_translation}
                          AND lang_translation=${request.lang_word}
                    )""".as[Words])
      if (res.isDefined)
        Ok(res.get)
      else
        InternalServerError(new Exception("Server error occurred, try again."))
    }
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
  def languageWords(userId: Int, request: DictRequest, token: Authorization): Output[Seq[Words]] = {
    if (!isTokenValid(token, userId))
      Unauthorized(new Exception("You don't have access to this information."))
    else {
      val res = getWords(userId, request.lang_word, request.lang_translation)
      if (res.isDefined) 
        Ok(res.get)
      else
        InternalServerError(new Exception("Server error occurred, try again."))
    }
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
    lang_word: String,
    lang_translation: String, 
    condition: String = ""
  ): Option[Seq[Words]] = {
    updateUserActivity(userId)

    fetch(
      sql"""(SELECT w.* FROM words w, user_words uw
            WHERE user_id=$userId AND id=word_id 
                AND lang_word=$lang_word
                AND lang_translation=$lang_translation)
            UNION (SELECT id, translation, word, lang_translation, lang_word
                   FROM words w, user_words uw
                   WHERE user_id=$userId AND id=word_id
                      AND lang_word=$lang_translation
                      AND lang_translation=$lang_word
                   )""".as[Words]
    )
  }

  /**
    * Fetch information about all the registered users.
    *
    * @param token the token of user sending the request.
    * 
    * Returns 200 status code and user information if token is valid,
    * 403 status code otherwise.
    */
  def getUserList(token: Authorization): Output[Seq[UserInfo]] = {
    if (!isTokenValid(token))
      Unauthorized(new Exception("Invalid credentials"))
    else {
      val data = fetch(
        sql"""SELECT id, username, joined, last_seen, coalesce(sum(score), 0) AS score, 
                      coalesce(sum(tries), 0) AS tries, count(word_id) AS word_count
              FROM users u
              LEFT JOIN user_words uw ON u.id=uw.user_id GROUP BY id
              ORDER BY tries DESC
              """.as[UserInfo])
      
      if (data.isDefined)
        Ok(data.get)
      else
        InternalServerError(new Exception("Server error occurred, try again."))
    }
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
  def getUserInfo(userId: Int, token: Authorization): Output[UserInfoExtended] = {
    if (!isTokenValid(token))
      Unauthorized(new Exception("Invalid credentials."))
    else {
      val user = fetchRow(
        sql"""SELECT id, username, sum(score) AS score, 
                    sum(tries) AS tries, last_seen, joined 
              FROM users u
              WHERE id=$userId GROUP BY id
              LEFT JOIN user_words uw ON id=user_id""".as[UserInfo])

      if (!user.isDefined)
        NotFound(new Exception("User not found."))

      val words = fetch(
        sql"""SELECT w.*, score, tries FROM words w, user_words uw
              WHERE user_id=$userId AND id=word_id""".as[UserWordPair])

      if (!words.isDefined) Ok( UserInfoExtended(user.get, Seq[UserWordPair]()) )
      else Ok( UserInfoExtended(user.get, words.get) )
    }
  }
}