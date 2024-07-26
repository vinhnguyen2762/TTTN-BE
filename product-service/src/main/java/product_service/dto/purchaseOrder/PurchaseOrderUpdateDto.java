package product_service.dto.purchaseOrder;

import product_service.dto.purchaseOrderDetail.PurchaseOrderDetailPostDto;

import java.util.List;

public record PurchaseOrderUpdateDto(
        List<PurchaseOrderDetailPostDto> list
) {
}
