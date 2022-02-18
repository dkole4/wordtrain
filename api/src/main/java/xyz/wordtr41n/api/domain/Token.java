package xyz.wordtr41n.api.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "token")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Token implements Serializable {
    
    private static final long serialVersionUID = 7654345678987654564L;

    @Id
    @Column(name="user_id")
    private Long id;

    @NotBlank
    private String token;

    @NotBlank
    private Timestamp expires;

}