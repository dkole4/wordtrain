package xyz.wordtr41n.api.controller;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import xyz.wordtr41n.api.domain.User;
import xyz.wordtr41n.api.dto.model.*;
import xyz.wordtr41n.api.dto.request.*;
import xyz.wordtr41n.api.dto.response.*;
import xyz.wordtr41n.api.exception.*;
import xyz.wordtr41n.api.service.UserService;
import xyz.wordtr41n.api.security.jwt.JwtUtils;
import xyz.wordtr41n.api.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class LoginController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
    
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
        return ResponseEntity.ok(
            new JwtResponse(jwt,
                            userDetails.getId(), 
                            userDetails.getUsername()));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) 
            throws URISyntaxException {
		User user = new User(registerRequest.getUsername(), 
							 encoder.encode(registerRequest.getPassword()));

        try {
            User registeredUser = userService.save(user);
            CurrentUser cuser = new CurrentUser(registeredUser);
            
            return ResponseEntity.created(new URI("/api/users/" + cuser.getId()))
                .body(cuser);
        } catch (ResourceAlreadyExistsException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Username is already taken!"));
        } catch (BadResourceException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}