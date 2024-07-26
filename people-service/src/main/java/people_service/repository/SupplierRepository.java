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

    Optional<Supplier> findByTaxId(String taxId);

    @Query("""
            select s
            from Supplier s
            where s.status = true and s.taxId = :taxId
            """)
    Optional<Supplier> findByTaxIdSearch(@Param("taxId") String taxId);
}
