package people_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
