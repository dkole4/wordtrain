package xyz.wordtr41n.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import xyz.wordtr41n.api.domain.Word;
import xyz.wordtr41n.api.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
 

@SpringBootTest
@TestPropertySource(
  locations = "/application-integrationtest.properties")
public class WordServiceJPATest {

    @Autowired 
    private WordService wordService;

    @Test
    public void testSaveUpdateDeleteWord() throws Exception {
        Word word = new Word();
        word.setWord("koira");
        word.setTranslation("dog");
        word.setLangWord("finnish");
        word.setLangTranslation("english");

        // Test normalization
        word.normalize();
        assertEquals("dog", word.getWord());
        assertEquals("koira", word.getTranslation());
        assertEquals("english", word.getLangWord());
        assertEquals("finnish", word.getLangTranslation());

        // Save Word
		wordService.save(word);
        assertNotNull(word.getId());

        Word foundWord = wordService.findById(word.getId());
        assertEquals(word.getWord(), foundWord.getWord());
        assertEquals(word.getTranslation(), foundWord.getTranslation());
        assertEquals(word.getLangWord(), foundWord.getLangWord());
        assertEquals(word.getLangTranslation(), foundWord.getLangTranslation());

        // Update word
        word.setWord("dogs");
        wordService.update(word);

        // test after update
        foundWord = wordService.findById(word.getId());
        assertEquals("dogs", foundWord.getWord());

        // test delete
        wordService.deleteById(word.getId());

        // query after delete
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            wordService.findById(word.getId());
        });

        assertTrue(thrown.getMessage().contains("Cannot find word with id:"));
    }    
}