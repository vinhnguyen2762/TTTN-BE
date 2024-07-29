package product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import product_service.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
            select p
            from Product p
            where p.status = true
            """)
    List<Product> findAll();

    @Query("SELECT COALESCE(COUNT(p), 0) FROM Product p WHERE p.status = true")
    Long countProductsByStatusTrue();

    Optional<Product> findByName(String name);
}
