package product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import product_service.model.Order;
import product_service.model.PurchaseOrder;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    @Query("""
            select p
            from PurchaseOrder p
            left join fetch p.purchaseOrderDetails
            where p.status != 'DELETED'
            """)
    List<PurchaseOrder> findAll();
    @Query("""
            select p
            from PurchaseOrder p
            left join fetch p.purchaseOrderDetails
            where p.id = :id
            """)
    Optional<PurchaseOrder> findById(@Param("id") Long id);
}
