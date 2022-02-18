package xyz.wordtr41n.api.domain;

import java.io.Serializable;
import java.util.Set;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "userWords" })
public class User implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Timestamp joined = new Timestamp(System.currentTimeMillis());

    @Column(name = "last_seen")
    private Timestamp lastSeen = new Timestamp(System.currentTimeMillis());

    @OneToMany(mappedBy = "user")
    Set<UserWord> userWords;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}