package xyz.wordtr41n.api.service;

import xyz.wordtr41n.api.domain.Word;
import xyz.wordtr41n.api.dto.model.UserWordPair;
import xyz.wordtr41n.api.exception.BadResourceException;
import xyz.wordtr41n.api.exception.ResourceAlreadyExistsException;
import xyz.wordtr41n.api.exception.ResourceNotFoundException;
import xyz.wordtr41n.api.repository.WordRepository;
import xyz.wordtr41n.api.specification.WordSpecification;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    private boolean existsById(Long id) {
        return wordRepository.existsById(id);
    }

    private Word findByWordAndTranslation(Word word) {
        word.normalize();
        Specification<Word> spec = new WordSpecification(word);
        List<Word> words = wordRepository.findAll(spec);
        if (words.size() > 0)
            return words.get(0);
        return null;
    }

    public List<Word> findAll() {
        return wordRepository.findAll();
    }

    public Word findById(Long id) throws ResourceNotFoundException {
        Word word = wordRepository.findById(id).orElse(null);

        if (word == null) {
            throw new ResourceNotFoundException("Cannot find word with id: " + id);
        }
        else return word;
    }

    public List<UserWordPair> findByUserId(Long userId) {
        return wordRepository.getUserWordPairs(userId);
    }

    public List<UserWordPair> findByUserIdAndLanguage(
            Long userId, String langWord, String langTranslation, Integer wordNumber) 
            throws BadResourceException {
        if (ObjectUtils.isEmpty(langWord) || ObjectUtils.isEmpty(langTranslation))
            return wordRepository.getUserWordPairs(userId);
        
        if (wordNumber == null) {
            if (langWord.compareTo(langTranslation) > 0)
                return wordRepository.getUserWordPairsByLanguage(
                    userId, langTranslation, langWord);

            return wordRepository.getUserWordPairsByLanguage(
                userId, langWord, langTranslation);
        }
        else if (wordNumber <= 0) {
            throw new BadResourceException("Word number is invalid, use only positive values");
        }

        if (langWord.compareTo(langTranslation) > 0)
            return wordRepository.getUserWordPairsByLanguage(
                userId, langTranslation, langWord, 
                PageRequest.of(0, wordNumber, 
                    JpaSort.unsafe(Sort.Direction.ASC, "uw.score / (uw.tries + 1)")));
        
        return wordRepository.getUserWordPairsByLanguage(
            userId, langWord, langTranslation, 
            PageRequest.of(0, wordNumber,
                JpaSort.unsafe(Sort.Direction.ASC, "uw.score / (uw.tries + 1)")));
    }

    public List<Word> findAllByLanguage(String langWord, String langTranslation) {
        Word filter = new Word();
        filter.setLangWord(langWord);
        filter.setLangTranslation(langTranslation);
        Specification<Word> spec = new WordSpecification(filter);

        List<Word> words = new ArrayList<>();
        wordRepository.findAll(spec).forEach(words::add);

        return words;
    }

    public Word save(Word word) throws BadResourceException {
        if (!ObjectUtils.isEmpty(word.getWord()) && !ObjectUtils.isEmpty(word.getTranslation()) &&
                !ObjectUtils.isEmpty(word.getLangWord()) && !ObjectUtils.isEmpty(word.getLangTranslation())) {
            
            if (word.getId() != null) { 
                throw new BadResourceException("New word should not contain id.");
            }

            Word same = findByWordAndTranslation(word);
            if (same != null)
                return same;
            
            word.normalize();
            return wordRepository.save(word);
            
        }
        else {
            BadResourceException exc = new BadResourceException("Failed to save word");
            exc.addErrorMessage("Word is null or has empty fields");
            throw exc;
        }
    }

    public List<Word> saveAll(List<Word> words) throws BadResourceException {
        List<Word> newWords = new ArrayList<>();
        
        for (Word w : words) {
            newWords.add(save(w));
        }

        return newWords;
    }

    public void update(Word word) 
            throws BadResourceException, ResourceNotFoundException {
        if (!ObjectUtils.isEmpty(word.getWord()) && !ObjectUtils.isEmpty(word.getTranslation())) {
            if (!existsById(word.getId())) {
                throw new ResourceNotFoundException("Cannot find word with id: " + word.getId());
            }
            word.normalize();
            wordRepository.save(word);
        }
        else {
            BadResourceException exc = new BadResourceException("Failed to save word");
            exc.addErrorMessage("Word is null or has empty fields");
            throw exc;
        }
    }

    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!existsById(id)) { 
            throw new ResourceNotFoundException("Cannot find word with id: " + id);
        }
        else {
            wordRepository.deleteById(id);
        }
    }

    public Long count() {
        return wordRepository.count();
    }
}