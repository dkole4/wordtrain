package xyz.wordtr41n.api.dto.model;

public interface UserWordPair {
    Long getId();
    String getWord();
    String getTranslation();
    String getLangWord();
    String getLangTranslation();
    Integer getScore();
    Integer getTries();
}