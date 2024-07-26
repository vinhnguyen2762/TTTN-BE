package product_service.dto.purchaseOrderDetail;

import product_service.model.PurchaseOrderDetail;

public record PurchaseOderDetailAdminDto(
        Long id,
        Long productId,
        String productName,
        String supplyPrice,
        String quantity
) {
    public static PurchaseOderDetailAdminDto fromPurchaseOrderDetail(PurchaseOrderDetail purchaseOrderDetail, String productName) {
        return new PurchaseOderDetailAdminDto(
                purchaseOrderDetail.getId(),
                purchaseOrderDetail.getProductId(),
                productName,
                purchaseOrderDetail.getSupplyPrice().toString(),
                purchaseOrderDetail.getQuantity().toString()
        );
    }
}
