package xyz.wordtr41n.api.dto.response;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String token;

	private String type = "Bearer";

	private Long id;

	private String username;

	public JwtResponse(String accessToken, Long id, String username) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
	}
}