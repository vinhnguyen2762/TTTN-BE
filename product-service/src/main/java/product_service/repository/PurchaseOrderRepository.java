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
            where p.status != 'DELETED' and p.smallTraderId = :id
            """)
    List<PurchaseOrder> findBySmallTraderId(@Param("id") Long id);
    @Query("""
            select p
            from PurchaseOrder p
            left join fetch p.purchaseOrderDetails
            where p.id = :id
            """)
    Optional<PurchaseOrder> findById(@Param("id") Long id);

    @Query("""
            SELECT COALESCE(SUM(pod.quantity * pod.supplyPrice), 0)
            FROM PurchaseOrder po JOIN po.purchaseOrderDetails pod
            WHERE YEAR(po.deliveryDate) = :year 
            AND MONTH(po.deliveryDate) = :month 
            AND po.status = 'PAID'
            AND po.smallTraderId = :id
            """)
    Long findMoneyPurchaseByMonthAndYear(@Param("year") Integer year, @Param("month") Integer month, @Param("id") Long id);

    @Query("""
            SELECT COALESCE(COUNT(po), 0) 
            FROM PurchaseOrder po 
            WHERE po.status = 'PAID' 
            AND YEAR(po.deliveryDate) = :year 
            AND MONTH(po.deliveryDate) = :month 
            AND po.smallTraderId = :id
            """)
    Long countPurchaseOrdersByStatusAndMonth(@Param("year") Integer year, @Param("month") Integer month, @Param("id") Long id);
}
