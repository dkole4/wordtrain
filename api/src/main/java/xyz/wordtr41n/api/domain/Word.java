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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "words")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@JsonIgnoreProperties({ "userWords" })
public class Word implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String word;

    @NotBlank
    private String translation;

    @NotBlank
    @Column(name="lang_word")
    private String langWord;

    @NotBlank
    @Column(name="lang_translation")
    private String langTranslation;

    @OneToMany(mappedBy = "word")
    Set<UserWord> userWords;

    public boolean compareToWord(Word other) {
        return (word.equalsIgnoreCase(other.getWord()) && translation.equalsIgnoreCase(other.getTranslation()))
            || (word.equalsIgnoreCase(other.getTranslation()) && translation.equalsIgnoreCase(other.getWord()));
    }

    public void normalize() {
        if (langWord.compareTo(langTranslation) > 0) {
            String tmp = langWord;
            this.langWord = langTranslation;
            this.langTranslation = tmp;

            tmp = word;
            this.word = translation;
            this.translation = tmp;
        }
    }
}