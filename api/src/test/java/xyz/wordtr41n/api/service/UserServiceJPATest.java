package xyz.wordtr41n.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import xyz.wordtr41n.api.domain.User;
import xyz.wordtr41n.api.dto.model.UserProfile;
import xyz.wordtr41n.api.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
 

@SpringBootTest
@TestPropertySource(
  locations = "/application-integrationtest.properties")
public class UserServiceJPATest {

    @Autowired 
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @Test
    public void testSaveUpdateDeleteUser() throws Exception {
        User user = new User("username", encoder.encode("passwd"));

        // Save User
		userService.save(user);
        assertNotNull(user.getId());

        UserProfile foundUser = userService.findById(user.getId());
        assertEquals("username", foundUser.getUsername());

        // Update User
        user.setUsername("nameuser");
        userService.update(user);

        // test after update
        foundUser = userService.findById(user.getId());
        assertEquals("nameuser", foundUser.getUsername());

        // test delete
        userService.deleteById(user.getId());

        // query after delete
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            userService.findById(user.getId());
        });

        assertTrue(thrown.getMessage().contains("Cannot find user with id:"));
    }    
}