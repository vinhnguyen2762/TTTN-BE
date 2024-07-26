package product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import product_service.model.PurchaseOrderDetail;

public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Long> {
}
