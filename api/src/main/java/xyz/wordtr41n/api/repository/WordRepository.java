package xyz.wordtr41n.api.repository;
 
import xyz.wordtr41n.api.domain.Word;
import xyz.wordtr41n.api.dto.model.UserWordPair;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Collection;

public interface WordRepository extends JpaRepository<Word, Long>, 
        JpaSpecificationExecutor<Word> {
    @Query("SELECT w.id as id, w.word as word, w.translation as translation, " + 
            "w.langWord as langWord, w.langTranslation as langTranslation, " + 
            "uw.score as score, uw.tries as tries " +
            "FROM Word w LEFT JOIN w.userWords uw " +
            "WHERE uw.userId = :userId")
    List<UserWordPair> getUserWordPairs(@Param("userId") Long userId);

    @Query("SELECT w.id as id, w.word as word, w.translation as translation, " + 
            "w.langWord as langWord, w.langTranslation as langTranslation, " + 
            "uw.score as score, uw.tries as tries " +
            "FROM Word w LEFT JOIN w.userWords uw " +
            "WHERE uw.userId = :userId " +
            "AND w.langWord = :langWord AND w.langTranslation = :langTranslation")
    List<UserWordPair> getUserWordPairsByLanguage(
        @Param("userId") Long userId, 
        @Param("langWord") String langWord, 
        @Param("langTranslation") String langTranslation,
        Pageable pageable);
    
    @Query("SELECT w.id as id, w.word as word, w.translation as translation, " + 
            "w.langWord as langWord, w.langTranslation as langTranslation, " + 
            "uw.score as score, uw.tries as tries " +
            "FROM Word w LEFT JOIN w.userWords uw " +
            "WHERE uw.userId = :userId " +
            "AND w.langWord = :langWord AND w.langTranslation = :langTranslation " +
            "ORDER BY uw.score / (uw.tries + 1) ASC")
    List<UserWordPair> getUserWordPairsByLanguage(
        @Param("userId") Long userId, 
        @Param("langWord") String langWord, 
        @Param("langTranslation") String langTranslation);
}