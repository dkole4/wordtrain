package xyz.wordtr41n.api.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.MapsId;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@IdClass(UserWordPK.class)
@Table(name = "user_words")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@JsonIgnoreProperties({ "user", "word", "id" })
public class UserWord {
    
    @Id
    @Column(name="user_id")
    private Long userId;

    @Id
    @Column(name="word_id")
    private Long wordId;

    private int score = 0;

    private int tries = 0;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("wordId")
    @JoinColumn(name = "word_id")
    Word word;

    public UserWord() { }

    public UserWord(Long userId, Long wordId) {
        this.userId = userId;
        this.wordId = wordId;
    }

    public UserWord(UserWordPK id) {
        this.userId = id.getUserId();
        this.wordId = id.getWordId();
    }

    public UserWordPK getId() {
        return new UserWordPK(
            userId,
            wordId
        );
    }

    public void setId(UserWordPK id) {
        this.userId = id.getUserId();
        this.wordId = id.getWordId();
    }

    public boolean isValid() {
        return score >= 0 && tries >= 0 && tries >= score;
    }
}
