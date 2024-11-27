package people_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import people_service.model.ConfirmationToken;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);

    @Query("""
            select c
            from ConfirmationToken c
            where c.smallTraderId = :id
            """)
    Optional<ConfirmationToken> findBySmallTraderId(@Param("id") Long id);
}
