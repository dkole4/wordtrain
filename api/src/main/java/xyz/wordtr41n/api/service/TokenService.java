package xyz.wordtr41n.api.service;

import xyz.wordtr41n.api.domain.Token;
import xyz.wordtr41n.api.exception.BadResourceException;
import xyz.wordtr41n.api.exception.ResourceAlreadyExistsException;
import xyz.wordtr41n.api.exception.ResourceNotFoundException;
import xyz.wordtr41n.api.repository.TokenRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    private boolean existsById(Long id) {
        return tokenRepository.existsById(id);
    }

    public Token findById(Long id) throws ResourceNotFoundException {
        Token token = tokenRepository.findById(id).orElse(null);

        if (token == null) {
            throw new ResourceNotFoundException("Cannot find token with id: " + id);
        }
        else return token;
    }

    public List<Token> findAll(int pageNumber, int rowPerPage) {
        List<Token> token = new ArrayList<>();
        tokenRepository.findAll(PageRequest.of(pageNumber - 1, rowPerPage)).forEach(token::add);
        return token;
    }

    public Token save(Token token) throws BadResourceException, ResourceAlreadyExistsException {
        if (token.getId() != null && !ObjectUtils.isEmpty(token.getToken())) {
            if (existsById(token.getId())) { 
                throw new ResourceAlreadyExistsException("User with id: " + token.getId() +
                        " already has token");
            }
            return tokenRepository.save(token);
        }
        else {
            BadResourceException exc = new BadResourceException("Failed to save token");
            exc.addErrorMessage("Token is null or has empty fields");
            throw exc;
        }
    }

    public void update(Token token) 
            throws BadResourceException, ResourceNotFoundException {
        if (!ObjectUtils.isEmpty(token.getToken()) && token.getExpires() != null) {
            if (!existsById(token.getId())) {
                throw new ResourceNotFoundException("Cannot find user token with id: " + token.getId());
            }
            tokenRepository.save(token);
        }
        else {
            BadResourceException exc = new BadResourceException("Failed to save token");
            exc.addErrorMessage("Token is null or has empty fields");
            throw exc;
        }
    }

    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!existsById(id)) { 
            throw new ResourceNotFoundException("Cannot find user token with id: " + id);
        }
        else {
            tokenRepository.deleteById(id);
        }
    }

    public Long count() {
        return tokenRepository.count();
    }
}