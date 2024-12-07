package people_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import people_service.model.Customer;
import people_service.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Query("""
            select s
            from Supplier s
            where s.status = true
            """)
    List<Supplier> findAll();

    @Query("""
            select s
            from Supplier s
            where s.status = true and s.smallTrader.id = :id
            """)
    List<Supplier> findBySmallTraderId(@Param("id") Long id);

    @Query("""
            select s
            from Supplier s
            where s.smallTrader.id = :id and s.taxId = :taxId
            """)
    Optional<Supplier> findByTaxIdSmallTraderId(@Param("id") Long id, @Param("taxId") String taxId);
    @Query("""
            select s
            from Supplier s
            where s.smallTrader.id = :id and s.email = :email
            """)
    Optional<Supplier> findByEmailSmallTraderId(@Param("id") Long id, @Param("email") String email);

    @Query("""
            select s
            from Supplier s
            where s.smallTrader.id = :id and s.phoneNumber = :phoneNumber
            """)
    Optional<Supplier> findByPhoneNumberSmallTraderId(@Param("id") Long id, @Param("phoneNumber") String phoneNumber);

    @Query("""
            select s
            from Supplier s
            where s.status = true and s.taxId = :taxId
            """)
    Optional<Supplier> findByTaxIdSearch(@Param("taxId") String taxId);

    @Query("SELECT COALESCE(COUNT(s), 0) FROM Supplier s WHERE s.status = true and s.smallTrader.id = :id")
    Long countSupplierSmallTrader(@Param("id") Long id);
}
