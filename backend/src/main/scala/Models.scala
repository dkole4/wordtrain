package backend

import java.time.LocalDateTime
import com.twitter.finagle.postgres._
import com.twitter.finagle.postgres.generic._


/** Handler of database related operations. */
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

object ResponseModels {
  /**
    * Class representing a word pair.
    *
    * @param id the id of word pair.
    * @param word the word of word pair.
    * @param translation the translation of word pair.
    * @param lang_word the language of word.
    * @param lang_translation the language of translation.
    */
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

    // def apply(
    //   id: Int,
    //   word: String,
    //   translation: String,
    //   lang_word: String,
    //   lang_translation: String,
    //   score: Int,
    //   tries: Int
    // ): UserWordPair = {
    //   UserWordPair(id, word, translation, lang_word, lang_translation, score, tries)
    // }

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

  // object UserInfo {
  //   def apply(
  //     id:        Int,
  //     username:  String,
  //     joined:    LocalDateTime,
  //     last_seen: LocalDateTime,
  //   ): UserInfo = {
  //     UserInfo(id, username, joined, last_seen, 0, 0, Seq[UserWordPair]())
  //   }

  //   def apply(
  //     id:        Int,
  //     username:  String,
  //     joined:    LocalDateTime,
  //     last_seen: LocalDateTime,
  //     score:     Int,
  //     tries:     Int
  //   ): UserInfo = {
  //     UserInfo(id, username, joined, last_seen, score, tries, Seq[UserWordPair]())
  //   }

  //   def apply(
  //     user: UserInfo,
  //     words: Seq[UserWordPair]
  //   ): UserInfo = {
  //     UserInfo(
  //       user.id, user.username, user.joined,
  //       user.last_seen, user.score, user.tries, words
  //     )
  //   }

  //   implicit val userInfo: RowDecoder[UserInfo] = 
  //     RowDecoder(row: Row): UserInfo  {
  //       UserInfo(
  //         row.get[Int]("id"),
  //         row.get[String]("username"),
  //         row.get[LocalDateTime]("joined"),
  //         row.get[LocalDateTime]("last_seen")
  //       )
  //     }
  // }
}