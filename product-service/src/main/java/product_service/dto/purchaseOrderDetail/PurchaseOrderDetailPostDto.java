package product_service.dto.purchaseOrderDetail;

public record PurchaseOrderDetailPostDto(
        Long productId,
        String supplyPrice,
        String quantity
) {
}
