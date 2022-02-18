package xyz.wordtr41n.api.repository;
 
import xyz.wordtr41n.api.domain.Token;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.sql.Timestamp;
import java.util.List;

public interface TokenRepository extends PagingAndSortingRepository<Token, Long>, 
    JpaSpecificationExecutor<Token> {
        boolean existsByIdAndExpiresLessThanEqual(Long id, Timestamp ts);
}