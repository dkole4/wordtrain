package xyz.wordtr41n.api.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
	@NotBlank
    @Size(min = 3, max = 32)
	private String username;

	@NotBlank
    @Size(min = 6, max = 40)
	private String password;
}