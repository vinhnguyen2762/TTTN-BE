package people_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import people_service.model.Customer;
import people_service.model.Employee;
import people_service.model.SmallTrader;

import java.util.List;
import java.util.Optional;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByPhoneNumber(String phoneNumber);


    @Query("""
            select e
            from Employee e
            where e.status = true and e.smallTrader.id = :id
            """)
    List<Employee> findBySmallTraderId(@Param("id") Long id);

}
