package product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import product_service.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
            select o
            from Order o
            left join fetch o.orderDetails
            where o.status != 'DELETED'
            """)
    List<Order> findAll();

    @Query("""
            select o
            from Order o
            left join fetch o.orderDetails
            where o.id = :id
            """)
    Optional<Order> findById(@Param("id") Long id);
}
