package xyz.wordtr41n.api.controller;

import xyz.wordtr41n.api.domain.Word;
import xyz.wordtr41n.api.domain.UserWord;
import xyz.wordtr41n.api.domain.UserWordPK;
import xyz.wordtr41n.api.dto.model.UserWordPair;
import xyz.wordtr41n.api.exception.*;
import xyz.wordtr41n.api.service.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/words")
public class WordController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WordService wordService;

    @Autowired
    private UserWordService userWordService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Word>> findAllWords(
            @RequestParam(required=false) String langWord,
            @RequestParam(required=false) String langTranslation) {
    
        if (ObjectUtils.isEmpty(langWord) || ObjectUtils.isEmpty(langTranslation)) {
            return ResponseEntity.ok(wordService.findAll());
        } else {
            if (langWord.compareTo(langTranslation) > 0) {
                List<Word> words = wordService.findAllByLanguage(langTranslation, langWord);
                return ResponseEntity.ok(words);
            } else {
                List<Word> words = wordService.findAllByLanguage(langWord, langTranslation);
                return ResponseEntity.ok(words);
            }
        }
    }

    @GetMapping(value = "/{wordId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Word> findWordById(@PathVariable Long wordId) {
        try {
            Word word = wordService.findById(wordId);
            return ResponseEntity.ok(word);
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
        }
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserWordPair>> findWordsByUserId(
            @PathVariable Long userId,
            @RequestParam(required=false) String langWord,
            @RequestParam(required=false) String langTranslation,
            @RequestParam(required=false) Integer wordNumber) {
        try {
            if (!userService.existsById(userId))
                throw new ResourceNotFoundException("User not found");
            
            List<UserWordPair> words = wordService.findByUserIdAndLanguage(
                userId, langTranslation, langWord, wordNumber);
            return ResponseEntity.ok(words); 
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (BadResourceException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping(value = "/user/{userId}")
    public ResponseEntity<List<Word>> addWords(@Valid @RequestBody List<Word> words,
            @PathVariable Long userId) {
        try {
            if (!userService.existsById(userId))
                throw new ResourceNotFoundException("User not found");
            
            List<Word> newWords = wordService.saveAll(words);

            List<UserWord> uw = new ArrayList<>();
            newWords.forEach( word -> uw.add(new UserWord( userId, word.getId() )) );
            userWordService.saveAll(uw);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(newWords);
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ResourceAlreadyExistsException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (BadResourceException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping(value = "/user/{userId}")
    public ResponseEntity<Word> updateWord(@Valid @RequestBody Word word, 
            @PathVariable Long userId) {
        try {
            if (!userService.existsById(userId))
                throw new ResourceNotFoundException("User not found");

            if (word.getId() == null)
                throw new BadResourceException("Word id not found");

            UserWordPK userWordID = new UserWordPK(userId, word.getId());
            int wordCount = userWordService.countByWordId( word.getId() );

            if (wordCount == 0) {
                throw new ResourceNotFoundException();
            } else if (wordCount == 1) {
                wordService.update( word );
                return ResponseEntity.ok().body(word);
            } else {
                word.setId(null);
                userWordService.deleteById( userWordID );
                Word newWord = wordService.save( word );
                userWordService.save( new UserWord(userId, newWord.getId()) );
                return ResponseEntity.ok().body(newWord);
            }
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BadResourceException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ResourceAlreadyExistsException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping(value = "/userWord/{userId}")
    public ResponseEntity<List<UserWord>> updateScores(
            @Valid @RequestBody List<UserWord> changes,
            @PathVariable Long userId) {
        try {
            if (!userService.existsById(userId))
                throw new ResourceNotFoundException("User not found");

            if (changes.size() < 1)
                throw new BadResourceException("Changes list is empty");

            List<UserWord> updated = userWordService.updateScores(changes);
            return ResponseEntity.ok().body(updated);
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BadResourceException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping(path="/user/{userId}")
    public ResponseEntity<Void> deleteWordById(@PathVariable Long userId,
            @RequestParam(required=true) Long wordId) {
        try {
            if (!userService.existsById(userId))
                throw new ResourceNotFoundException("User not found");

            UserWordPK userWordID = new UserWordPK(userId, wordId);
            int wordCount = userWordService.countByWordId( wordId );
            
            if (wordCount == 0) {
                throw new ResourceNotFoundException("Word not found");
            } else if (wordCount == 1) {
                userWordService.deleteById( userWordID );
                wordService.deleteById( wordId );
            } else {
                userWordService.deleteById( userWordID );
            }

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        } 
    }
}