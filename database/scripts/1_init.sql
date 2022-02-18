CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  username TEXT NOT NULL,
  password TEXT NOT NULL,
  joined TIMESTAMP NOT NULL DEFAULT NOW(),
  last_seen TIMESTAMP NOT NULL DEFAULT NOW(),
  UNIQUE(username)
);

CREATE TABLE token (
  user_id BIGSERIAL NOT NULL,
  token TEXT NOT NULL,
  expires TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY(user_id),
  FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE words (
  id BIGSERIAL PRIMARY KEY,
  word TEXT NOT NULL,
  translation TEXT NOT NULL,
  lang_word TEXT NOT NULL,
  lang_translation TEXT NOT NULL,
  UNIQUE(word, translation, lang_word, lang_translation)
);

CREATE TABLE user_words (
  user_id BIGSERIAL NOT NULL,
  word_id BIGSERIAL NOT NULL,
  score INTEGER NOT NULL DEFAULT 0,
  tries INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY(user_id, word_id),
  FOREIGN KEY(user_id) REFERENCES users(id),
  FOREIGN KEY(word_id) REFERENCES words(id) ON DELETE CASCADE
);