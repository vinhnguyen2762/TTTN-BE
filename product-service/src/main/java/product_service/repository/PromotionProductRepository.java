package product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import product_service.model.PromotionProduct;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {
}
