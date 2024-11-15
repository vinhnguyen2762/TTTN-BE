package people_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import people_service.model.ConfirmationToken;
import people_service.model.ConfirmationTokenCustomer;

import java.util.Optional;

@Repository
public interface ConfirmationTokenCustomerRepository extends JpaRepository<ConfirmationTokenCustomer, Long> {
    Optional<ConfirmationTokenCustomer> findByToken(String token);
}
