package backend

import java.time.LocalDateTime
import com.twitter.finagle.postgres._
import com.twitter.finagle.postgres.generic._


/** Models of database tables. **/
object DBModels { 
  case class Users(
    id:        Int,
    username:  String,
    passwd:    String,
    joined:    LocalDateTime,
    last_seen: LocalDateTime
  )

  case class Token(
    user_id: Int,
    token:   String,
    expires: LocalDateTime
  )

  case class Words(
    id:               Int,
    word:             String,
    translation:      String,
    lang_word:        String,
    lang_translation: String
  )

  case class UserWords(
    user_id: Int,
    word_id: Int,
    score:   Int,
    tries:   Int
  )
}

/** Models of response bodies. **/
object ResponseModels {

  case class WordPair (
    word:             String,
    translation:      String, 
    lang_word:        String, 
    lang_translation: String
  )

  case class UserWordPair(
    id:               Int,
    word:             String,
    translation:      String,
    lang_word:        String,
    lang_translation: String,
    score:            Int,
    tries:            Int
  )

  object UserWordPair {
    def apply(
      word: DBModels.Words,
      score: Int = 0,
      tries: Int = 0
    ): UserWordPair = {
      UserWordPair(
        word.id,
        word.word,
        word.translation,
        word.lang_word,
        word.lang_translation,
        score,
        tries
      )
    }

    def toWordPair(u: UserWordPair) = 
      WordPair(u.word, u.translation, u.lang_word, u.lang_translation)
  }

  case class UserInfo(
    id:         Int,
    username:   String,
    joined:     LocalDateTime,
    last_seen:  LocalDateTime,
    score:      Long,
    tries:      Long,
    word_count: Long
  )

  case class UserInfoExtended(
    id:        Int,
    username:  String,
    joined:    LocalDateTime,
    last_seen: LocalDateTime,
    score:     Long,
    tries:     Long,
    words:     Seq[UserWordPair]
  )

  object UserInfoExtended {
    def apply(
      user: UserInfo,
      words: Seq[UserWordPair]
    ): UserInfoExtended = {
      UserInfoExtended(
        user.id, user.username, user.joined,
        user.last_seen, user.score, user.tries, words
      )
    }
  }
}