package people_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import people_service.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    @Query("""
            select c
            from Customer c
            where c.status = true and c.phoneNumber = :phoneNumber
            """)
    Optional<Customer> findByPhoneNumberSearch(@Param("phoneNumber") String phoneNumber);

    @Query("""
            select c
            from Customer c
            where c.status = true
            """)
    List<Customer> findAll();

    @Query("""
            select c
            from Customer c
            where c.status = true and c.smallTrader.id = :id
            """)
    List<Customer> findBySmallTraderId(@Param("id") Long id);

    @Query("SELECT COALESCE(COUNT(c), 0) FROM Customer c WHERE c.status = true and c.smallTrader.id = :id")
    Long countCustomerSmallTrader(@Param("id") Long id);
}
