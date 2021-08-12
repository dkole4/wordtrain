DO $$
DECLARE
u1 INTEGER;
w1 INTEGER;
w2 INTEGER;

BEGIN
INSERT INTO users(username, passwd) VALUES ('username', '123');
SELECT id INTO u1 FROM users WHERE username='username';

INSERT INTO words(word, translation, lang_word, lang_translation)
    VALUES ('koira', 'dog', 'finnish', 'english');
SELECT id INTO w1 FROM words WHERE word='koira';


INSERT INTO words(word, translation, lang_word, lang_translation)
    VALUES ('kissa', 'cat', 'finnish', 'english');
SELECT id INTO w2 FROM words WHERE word='kissa';

INSERT INTO user_words VALUES (u1, w1);
INSERT INTO user_words VALUES (u1, w2);
END $$;