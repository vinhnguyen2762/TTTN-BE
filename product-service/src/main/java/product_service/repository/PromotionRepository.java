package product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import product_service.model.Product;
import product_service.model.Promotion;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("""
            select p
            from Promotion p
            left join fetch p.promotionDetailList
            where p.status != 'DELETED' 
            """)
    List<Promotion> findAll();

    @Query("""
            select p
            from Promotion p
            left join fetch p.promotionDetailList
            where p.status != 'DELETED' and p.smallTraderId = :id
            """)
    List<Promotion> findBySmallTraderId(@Param("id") Long id);

    @Query("""
            select p
            from Promotion p
            left join fetch p.promotionDetailList
            where p.id = :id 
            """)
    Optional<Promotion> findById(@Param("id") Long id);

    @Query("""
            SELECT COALESCE(COUNT(p), 0)
            FROM Promotion p
            WHERE p.status = 'ACTIVE'
            AND YEAR(p.startDate) = :year
            AND YEAR(p.endDate) = :year
            AND (
                (MONTH(p.startDate) <= :month)
                AND (MONTH(p.endDate) >= :month)
            )
            AND p.smallTraderId = :id
            """)
    Long countPromotionsByStatusAndMonth(@Param("year") Integer year, @Param("month") Integer month, @Param("id") Long id);
}
