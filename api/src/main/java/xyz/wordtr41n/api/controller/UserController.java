package xyz.wordtr41n.api.controller;

import xyz.wordtr41n.api.domain.User;
import xyz.wordtr41n.api.dto.model.UserProfile;
import xyz.wordtr41n.api.exception.*;
import xyz.wordtr41n.api.service.UserService;
import xyz.wordtr41n.api.service.UserWordService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
@RequestMapping("/api/users")
public class UserController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int ROW_PER_PAGE = 5;

    @Autowired
    private UserService userService;

    @Autowired
    private UserWordService userWordService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserProfile>> findAllProfiles() {
        return ResponseEntity.ok(userService.findAllProfiles());
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> findProfileById(@PathVariable Long userId) {
        try {
            UserProfile user = userService.findById(userId);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping(value = "/{userId}")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user, 
            @PathVariable long userId) {
        try {
            user.setId(userId);
            userService.update(user);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            // log exception first, then return Not Found (404)
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BadResourceException ex) {
            // log exception first, then return Bad Request (400)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping(path="/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable long userId) {
        try {
            userService.deleteById(userId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}