//package people_service.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import people_service.model.Customer;
//import people_service.model.Producer;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface ProducerRepository extends JpaRepository<Producer, Long> {
//    @Query("""
//            select p
//            from Producer p
//            where p.smallTrader.id = :id and p.email = :email
//            """)
//    Optional<Producer> findByEmailSmallTraderId(@Param("id") Long id, @Param("email") String email);
//
//    @Query("""
//            select p
//            from Producer p
//            where p.smallTrader.id = :id and p.phoneNumber = :phoneNumber
//            """)
//    Optional<Producer> findByPhoneNumberSmallTraderId(@Param("id") Long id, @Param("phoneNumber") String phoneNumber);
//
//    @Query("""
//            select p
//            from Producer p
//            where p.status = true
//            """)
//    List<Producer> findAll();
//
//    @Query("""
//            select p
//            from Producer p
//            where p.status = true and p.smallTrader.id = :id
//            """)
//    List<Producer> findBySmallTrader(@Param("id") Long id);
//
//    @Query("SELECT COALESCE(COUNT(p), 0) FROM Producer p WHERE p.status = true")
//    Long countProducerByStatusTrue();
//}
