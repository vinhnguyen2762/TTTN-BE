package product_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "purchase_order_detail")
@NoArgsConstructor
public class PurchaseOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long supplyPrice;
    private Long productId;
    private Integer quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_oder_id")
    private PurchaseOrder purchaseOrder;

    public PurchaseOrderDetail(Long supplyPrice, Long productId, Integer quantity, PurchaseOrder purchaseOrder) {
        this.supplyPrice = supplyPrice;
        this.productId = productId;
        this.quantity = quantity;
        this.purchaseOrder = purchaseOrder;
    }
}
