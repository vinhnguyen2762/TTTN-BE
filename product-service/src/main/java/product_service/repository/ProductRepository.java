package product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
            select p
            from Product p
            where p.status = true and p.smallTraderId = :id
            """)
    List<Product> findBySmallTraderId(@Param("id") Long id);

    @Query("SELECT COALESCE(COUNT(p), 0) FROM Product p WHERE p.status = true and p.smallTraderId = :id")
    Long countProductsByStatusTrue(@Param("id") Long id);

    Optional<Product> findByName(String name);
    @Query("""
            select p
            from Product p
            where p.smallTraderId = :id and p.name = :name
            """)
    Optional<Product> findByNameSmallTraderId(@Param("id") Long id, String name);
}
