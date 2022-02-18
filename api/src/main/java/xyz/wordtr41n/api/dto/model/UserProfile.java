package xyz.wordtr41n.api.dto.model;

import java.sql.Timestamp;

public interface UserProfile {
    Long getId();
    String getUsername();
    Integer getWordCount();
    Integer getTries();
    Timestamp getJoined();
    Timestamp getLastSeen();
}