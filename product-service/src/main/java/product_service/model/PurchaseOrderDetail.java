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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_oder_id")
    private PurchaseOrder purchaseOrder;

    public PurchaseOrderDetail(Long supplyPrice, Product product, Integer quantity, PurchaseOrder purchaseOrder) {
        this.supplyPrice = supplyPrice;
        this.product = product;
        this.quantity = quantity;
        this.purchaseOrder = purchaseOrder;
    }
}
