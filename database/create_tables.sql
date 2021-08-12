CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username TEXT NOT NULL,
  passwd TEXT NOT NULL,
  last_seen TIMESTAMP NOT NULL DEFAULT NOW(),
  UNIQUE(username)
);

CREATE TABLE token (
  user_id INTEGER NOT NULL,
  token TEXT NOT NULL,
  expires TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY(user_id),
  FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE words (
  id SERIAL PRIMARY KEY,
  word TEXT NOT NULL,
  translation TEXT NOT NULL,
  lang_word TEXT NOT NULL,
  lang_translation TEXT NOT NULL,
  UNIQUE(word, translation, lang_word, lang_translation)
);

CREATE TABLE user_words (
  user_id INTEGER NOT NULL,
  word_id INTEGER NOT NULL,
  score INTEGER NOT NULL DEFAULT 0,
  tries INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY(user_id, word_id),
  FOREIGN KEY(user_id) REFERENCES users(id),
  FOREIGN KEY(word_id) REFERENCES words(id) ON DELETE CASCADE
);