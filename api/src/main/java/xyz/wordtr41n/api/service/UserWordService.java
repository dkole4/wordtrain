package xyz.wordtr41n.api.service;

import xyz.wordtr41n.api.domain.UserWord;
import xyz.wordtr41n.api.domain.UserWordPK;
import xyz.wordtr41n.api.exception.BadResourceException;
import xyz.wordtr41n.api.exception.ResourceAlreadyExistsException;
import xyz.wordtr41n.api.exception.ResourceNotFoundException;
import xyz.wordtr41n.api.specification.UserWordSpecification;
import xyz.wordtr41n.api.repository.UserWordRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserWordService {

    @Autowired
    private UserWordRepository userWordRepository;

    private boolean existsById(UserWordPK id) {
        return userWordRepository.existsById(id);
    }

    public UserWord findById(UserWordPK id) throws ResourceNotFoundException {
        UserWord userWord = userWordRepository.findById(id).orElse(null);

        if (userWord == null) {
            throw new ResourceNotFoundException("Cannot find UserWord with id: " + id);
        }
        else return userWord;
    }

    public List<Long> findByUserId(Long id) throws ResourceNotFoundException {
        List<Long> words = new ArrayList<>();
        userWordRepository
            .findByUserId(id)
            .forEach(uw -> words.add( uw.getId().getWordId() ));
        return words;
    }

    public Integer countById(UserWordPK id) {
        return userWordRepository.countByUserIdAndWordId(id.getUserId(), id.getWordId());
    }

    public Integer countByWordId(Long id) {
        return userWordRepository.countByWordId(id);
    }

    public Integer countByUserId(Long id) {
        return userWordRepository.countByUserId(id);
    }

    public List<UserWord> findAll(int pageNumber, int rowPerPage) {
        List<UserWord> userWords = new ArrayList<>();
        userWordRepository.findAll(PageRequest.of(pageNumber - 1, rowPerPage)).forEach(userWords::add);
        return userWords;
    }

    public UserWord save(UserWord userWord) throws BadResourceException, ResourceAlreadyExistsException {
        if (userWord.isValid()) {
            if (existsById(userWord.getId())) { 
                throw new ResourceAlreadyExistsException("UserWord with id: (" + userWord.getUserId() +
                        ", " + userWord.getWordId() + ") already exists");
            }
            return userWordRepository.save(userWord);
        }
        else {
            BadResourceException exc = new BadResourceException("Failed to save UserWord");
            exc.addErrorMessage("UserWord contains invalid data");
            throw exc;
        }
    }

    public List<UserWord> updateAll(List<UserWord> userWords) 
            throws BadResourceException, ResourceNotFoundException {
        if (userWords.size() == 0)
            throw new BadResourceException("UserWord list is empty");

        return userWordRepository.saveAll(userWords);
    }

    public List<UserWord> saveAll(List<UserWord> userWords) 
            throws BadResourceException, ResourceAlreadyExistsException {
        List<UserWord> newUserWords = new ArrayList<>();
        
        for (UserWord uw : userWords) {
            newUserWords.add(save(uw));
        }

        return newUserWords;
    }

    public void update(UserWord userWord) 
            throws BadResourceException, ResourceNotFoundException {
        if (userWord.isValid()) {
            if (!existsById(userWord.getId())) {
                throw new ResourceNotFoundException("Cannot find UserWord with id: " + userWord.getId());
            }
            userWordRepository.save(userWord);
        }
        else {
            BadResourceException exc = new BadResourceException("Failed to save UserWord");
            exc.addErrorMessage("UserWord contains invalid data");
            throw exc;
        }
    }

    public List<UserWord> updateScores(List<UserWord> changes) throws ResourceNotFoundException {
        return userWordRepository.saveAll(changes);
    }

    public void deleteById(UserWordPK id) throws ResourceNotFoundException {
        if (!existsById(id)) { 
            throw new ResourceNotFoundException("Cannot find UserWord with id: " + id);
        }
        else {
            userWordRepository.deleteById(id);
        }
    }

    public Long count() {
        return userWordRepository.count();
    }
}