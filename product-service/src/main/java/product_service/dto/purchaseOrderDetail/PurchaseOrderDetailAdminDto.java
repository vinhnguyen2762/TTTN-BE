package product_service.dto.purchaseOrderDetail;

import product_service.model.PurchaseOrderDetail;

public record PurchaseOrderDetailAdminDto(
        Long id,
        Long productId,
        String productName,
        String supplyPrice,
        String quantity
) {
    public static PurchaseOrderDetailAdminDto fromPurchaseOrderDetail(PurchaseOrderDetail purchaseOrderDetail, String productName) {
        return new PurchaseOrderDetailAdminDto(
                purchaseOrderDetail.getId(),
                purchaseOrderDetail.getProductId(),
                productName,
                purchaseOrderDetail.getSupplyPrice().toString(),
                purchaseOrderDetail.getQuantity().toString()
        );
    }
}
