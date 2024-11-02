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
            where o.status != 'DELETED' and o.smallTraderId = :id
            """)
    List<Order> findBySmallTraderId(@Param("id") Long id);

    @Query("""
            select o
            from Order o
            left join fetch o.orderDetails
            where o.id = :id
            """)
    Optional<Order> findById(@Param("id") Long id);
    List<Order> findByCustomerId(Long id);

    @Query("SELECT COALESCE(SUM(od.quantity * od.price), 0) " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month AND o.status = 'PAID' AND o.smallTraderId = :id")
    Long findRevenueByMonthAndYear(@Param("year") Integer year, @Param("month") Integer month, @Param("id") Long id);

    @Query("SELECT od.productId, COALESCE(SUM(od.quantity * od.price), 0) AS revenue " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month AND o.status = 'PAID' AND o.smallTraderId = :id " +
            "GROUP BY od.productId " +
            "ORDER BY revenue DESC")
    List<Object[]> findTop5ProductsByRevenue(@Param("year") Integer year, @Param("month") Integer month, @Param("id") Long id);

    @Query("""
            SELECT COALESCE(COUNT(o), 0) 
            FROM Order o 
            WHERE o.status = 'PAID' 
            AND YEAR(o.orderDate) = :year 
            AND MONTH(o.orderDate) = :month 
            AND o.smallTraderId = :id
            """)
    Long countOrdersByStatusAndMonth(@Param("year") Integer year, @Param("month") Integer month, @Param("id") Long id);
}
