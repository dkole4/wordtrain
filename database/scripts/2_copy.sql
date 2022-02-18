DO $$
DECLARE
u1 INTEGER;
w1 INTEGER;
w2 INTEGER;

BEGIN
INSERT INTO users(username, password) VALUES ('username', '123');
SELECT id INTO u1 FROM users WHERE username='username';

INSERT INTO words(word, translation, lang_word, lang_translation)
    VALUES ('dog', 'koira', 'english', 'finnish');
SELECT id INTO w1 FROM words WHERE word='dog';


INSERT INTO words(word, translation, lang_word, lang_translation)
    VALUES ('cat', 'kissa', 'english', 'finnish');
SELECT id INTO w2 FROM words WHERE word='cat';

INSERT INTO user_words VALUES (u1, w1);
INSERT INTO user_words VALUES (u1, w2);
END $$;