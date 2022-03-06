package xyz.wordtr41n.api.service;

import xyz.wordtr41n.api.domain.User;
import xyz.wordtr41n.api.dto.model.UserProfile;
import xyz.wordtr41n.api.exception.BadResourceException;
import xyz.wordtr41n.api.exception.ResourceAlreadyExistsException;
import xyz.wordtr41n.api.exception.ResourceNotFoundException;
import xyz.wordtr41n.api.repository.UserRepository;
import xyz.wordtr41n.api.specification.UserSpecification;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    private boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserProfile findById(Long id) throws ResourceNotFoundException {
        UserProfile user = userRepository.findProfileById(id);

        if (user == null) {
            throw new ResourceNotFoundException("Cannot find user with id: " + id);
        }
        else return user;
    }

    public List<User> findAll(int pageNumber, int rowPerPage) {
        List<User> users = new ArrayList<>();
        userRepository.findAll(PageRequest.of(pageNumber - 1, rowPerPage)).forEach(users::add);
        return users;
    }

    public List<UserProfile> findAllProfiles() {
        return userRepository.findAllProfiles();
    }

    public User save(User user) throws BadResourceException, ResourceAlreadyExistsException {
        if (!ObjectUtils.isEmpty(user.getUsername()) && !ObjectUtils.isEmpty(user.getPassword())) {
            if (user.getId() != null && existsById(user.getId())) { 
                throw new ResourceAlreadyExistsException("User with id: " + user.getId() +
                        " already exists");
            }
            if (ObjectUtils.isEmpty(user.getUsername()) || existsByUsername(user.getUsername())) {
                throw new ResourceAlreadyExistsException("Username is empty or User with nickname: " +
                        user.getUsername() + " already exists");
            }
            return userRepository.save(user);
        }
        else {
            BadResourceException exc = new BadResourceException("Failed to save user");
            exc.addErrorMessage("User is null or has empty fields");
            throw exc;
        }
    }

    public void update(User user) 
            throws BadResourceException, ResourceNotFoundException {
        if (!ObjectUtils.isEmpty(user.getUsername()) && !ObjectUtils.isEmpty(user.getPassword())) {
            if (!existsById(user.getId())) {
                throw new ResourceNotFoundException("Cannot find user with id: " + user.getId());
            }
            userRepository.save(user);
        }
        else {
            BadResourceException exc = new BadResourceException("Failed to save user");
            exc.addErrorMessage("User is null or has empty fields");
            throw exc;
        }
    }

    public void updateLastSeen(Long userId) throws ResourceNotFoundException {
        if (!existsById(userId)) {
            throw new ResourceNotFoundException("Cannot find user with id: " + userId);
        } else {
            userRepository.updateLastSeen(userId);
        }
    }

    public void deleteById(Long id) throws ResourceNotFoundException {
        if (!existsById(id)) { 
            throw new ResourceNotFoundException("Cannot find user with id: " + id);
        }
        else {
            userRepository.deleteById(id);
        }
    }

    public Long count() {
        return userRepository.count();
    }
}