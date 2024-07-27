package product_service.dto.purchaseOrder;

import product_service.dto.purchaseOrderDetail.PurchaseOrderDetailPostDto;

import java.util.List;

public record PurchaseOrderUpdateDto(
        String deliveryDate,
        List<PurchaseOrderDetailPostDto> list
) {
}
