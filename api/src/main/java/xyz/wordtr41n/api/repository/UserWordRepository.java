package xyz.wordtr41n.api.repository;
 
import xyz.wordtr41n.api.domain.UserWord;
import xyz.wordtr41n.api.domain.UserWordPK;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface UserWordRepository extends JpaRepository<UserWord, UserWordPK>, 
        JpaSpecificationExecutor<UserWord> {
    List<UserWord> findByUserId(Long userId);

    boolean existsByUserIdAndWordId(Long userId, Long wordId);
    
    Integer countByUserIdAndWordId(Long userId, Long wordId);

    Integer countByUserId(Long userId);

    Integer countByWordId(Long wordId);

    @Modifying
    @Transactional
    @Query("UPDATE UserWord uw SET uw.score = uw.score + ?1, uw.tries=uw.tries+1 " +
            "WHERE uw.userId = ?2 AND uw.wordId = ?3")
    void updateScore(Integer change, Long userId, Long wordId);
}