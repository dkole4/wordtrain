package xyz.wordtr41n.api.dto.model;

import xyz.wordtr41n.api.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CurrentUser {
    private Long id;
    private String username;
    private String token;

    public CurrentUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }
}