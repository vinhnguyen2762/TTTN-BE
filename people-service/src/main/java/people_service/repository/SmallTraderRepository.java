package people_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import people_service.model.SmallTrader;

import java.util.List;
import java.util.Optional;


@Repository
public interface SmallTraderRepository extends JpaRepository<SmallTrader, Long> {
    Optional<SmallTrader> findByEmail(String email);
    Optional<SmallTrader> findByPhoneNumber(String phoneNumber);
    Optional<SmallTrader> findById(Long id);

    @Query("""
            select e
            from SmallTrader e
            where e.status = true
            """)
    List<SmallTrader> findAll();

    @Query("SELECT COALESCE(COUNT(e), 0) FROM SmallTrader e WHERE e.status = true")
    Long countEmployeeByStatusTrue();
}
