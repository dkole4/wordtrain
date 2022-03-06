package xyz.wordtr41n.api;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import xyz.wordtr41n.api.dto.model.UserProfile;
import xyz.wordtr41n.api.dto.request.*;
import xyz.wordtr41n.api.service.UserService;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
  locations = "/application-integrationtest.properties")
class ApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Test
	public void testRegisterAndLogInUser() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setUsername("test");
		registerRequest.setPassword("passwd");

		this.mockMvc.perform(post("/api/auth/register")
					.content(asJsonString(registerRequest))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
					.andDo(print())
					.andExpect(status().isCreated())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.username", is("test")))
					.andExpect(jsonPath("$.id", is(1)));
		
		LoginRequest request = new LoginRequest();
		request.setUsername("test");
		request.setPassword("passwd");

		this.mockMvc.perform(post("/api/auth/login")
					.content(asJsonString(request))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.username", is("test")))
					.andExpect(jsonPath("$.id", is(1)))
					.andExpect(jsonPath("$.token", is(notNullValue())));
		
		UserProfile user = userService.findById(1L);
		assertEquals("test", user.getUsername());
		assertEquals(0, user.getWordCount());
		assertEquals(0, user.getTries());
	}

	public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
