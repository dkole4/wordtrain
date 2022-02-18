package xyz.wordtr41n.api.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWordPK implements Serializable {
    
    @Column(name="user_id")
    private Long userId;
    
    @Column(name="word_id")
    private Long wordId;

    public UserWordPK(Long userId, Long wordId) {
        this.userId = userId;
        this.wordId = wordId;
    }

    public UserWordPK() { }

    @Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserWordPK ck = (UserWordPK) o;
		return Objects.equals( userId, ck.getUserId() ) &&
				Objects.equals( wordId, ck.getWordId() );
	}

    @Override
	public int hashCode() {
		return Objects.hash( userId, wordId );
	}
}