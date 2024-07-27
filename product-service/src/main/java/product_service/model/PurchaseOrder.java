package product_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import product_service.enums.OrderStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "purchase_order")
@NoArgsConstructor
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long supplierId;
    private LocalDate createDate;
    private LocalDate deliveryDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();

    public PurchaseOrder(Long supplierId, LocalDate deliveryDate, LocalDate createDate) {
        this.supplierId = supplierId;
        this.deliveryDate = deliveryDate;
        this.createDate = createDate;
    }
}
