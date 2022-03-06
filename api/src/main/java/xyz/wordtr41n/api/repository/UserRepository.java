package xyz.wordtr41n.api.repository;
 
import xyz.wordtr41n.api.domain.User;
import xyz.wordtr41n.api.dto.model.UserProfile;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>, 
        JpaSpecificationExecutor<User> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);

    @Query("SELECT u.id AS id, u.username AS username, u.joined AS joined, " +
                "u.lastSeen AS lastSeen, COUNT(uw.tries) AS wordCount, " + 
                "COALESCE(SUM(uw.tries), 0) AS tries " +
            "FROM User u LEFT JOIN u.userWords uw " +
            "GROUP BY id")
    List<UserProfile> findAllProfiles();

    @Query("SELECT u.id AS id, u.username AS username, u.joined AS joined, " +
                "u.lastSeen AS lastSeen, COUNT(uw.tries) AS wordCount, " + 
                "COALESCE(SUM(uw.tries), 0) AS tries " +
            "FROM User u LEFT JOIN u.userWords uw " +
            "WHERE u.id = ?1 " +
            "GROUP BY u.id")
    UserProfile findProfileById(Long id);
}