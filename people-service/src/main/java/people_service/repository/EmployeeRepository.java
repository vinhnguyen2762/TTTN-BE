package people_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import people_service.dto.employee.EmployeeAdminDto;
import people_service.dto.employee.EmployeeUpdateDto;
import people_service.model.Customer;
import people_service.model.Employee;

import java.util.List;
import java.util.Optional;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    @Query("""
            select e
            from Employee e
            where e.status = true
            """)
    List<Employee> findAll();
}
