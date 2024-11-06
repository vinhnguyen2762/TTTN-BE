package people_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "debt_detail")
@Getter
@Setter
@NoArgsConstructor
public class DebtDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long debtAmount;
    private Long paidAmount = 0l;
    private LocalDate debtDate;
    private LocalDate paidDate = null;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producer_id")
    private Producer producer;

    public DebtDetail(Long debtAmount, LocalDate debtDate, Long paidAmount, LocalDate paidDate, Producer producer) {
        this.debtAmount = debtAmount;
        this.debtDate = debtDate;
        this.paidAmount = paidAmount;
        this.paidDate = paidDate;
        this.producer = producer;
    }
}
