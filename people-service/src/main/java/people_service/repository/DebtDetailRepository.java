package people_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import people_service.model.DebtDetail;
import people_service.model.Producer;

import java.util.List;
import java.util.Optional;

public interface DebtDetailRepository extends JpaRepository<DebtDetail, Long> {
}
