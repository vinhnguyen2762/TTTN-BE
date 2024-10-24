package product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import product_service.model.PromotionDetail;

import java.util.List;

public interface PromotionDetailRepository extends JpaRepository<PromotionDetail, Long> {
    List<PromotionDetail> findByProductId(Long productId);
}
