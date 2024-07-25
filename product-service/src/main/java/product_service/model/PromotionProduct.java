package product_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import product_service.enums.PromotionType;

@Entity
@Getter
@Setter
@Table(name = "promotion_product")
@NoArgsConstructor
public class PromotionProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    public PromotionProduct(Long productId, Promotion promotion) {
        this.productId = productId;
        this.promotion = promotion;
    }
}
