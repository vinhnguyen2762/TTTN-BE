package product_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import product_service.enums.PromotionStatus;
import product_service.enums.PromotionType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "promotion")
@NoArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private PromotionType type;
    private Long value;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private PromotionStatus status = PromotionStatus.PENDING;
    private Long smallTraderId;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<PromotionDetail> promotionDetailList = new ArrayList<>();

    public Promotion(String name, String description, PromotionType type, Long value, LocalDate startDate, LocalDate endDate, Long smallTraderId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
        this.smallTraderId = smallTraderId;
    }
}
