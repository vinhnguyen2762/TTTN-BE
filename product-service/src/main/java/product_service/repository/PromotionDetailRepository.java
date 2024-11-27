package product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import product_service.model.PromotionDetail;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Long> {
    @Query("""
        select pd
        from PromotionDetail pd
        where pd.product.id = :productId
        """)
    List<PromotionDetail> findByProductId(@Param("productId") Long productId);

}
