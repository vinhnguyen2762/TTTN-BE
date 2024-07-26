package product_service.dto.purchaseOrder;

import product_service.dto.purchaseOrderDetail.PurchaseOderDetailAdminDto;
import product_service.model.PurchaseOrder;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record PurchaseOrderAdminDto(
        Long id,
        Long supplierId,
        String taxId,
        String supplierName,
        String deliveryTime,
        String status,
        List<PurchaseOderDetailAdminDto> list
) {
    public static PurchaseOrderAdminDto fromPurchaseOrder(PurchaseOrder purchaseOrder, String taxId, String supplierName, List<PurchaseOderDetailAdminDto> list) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String status = purchaseOrder.getStatus().name().equals("PENDING") ? "Chờ thanh toán" : "Đã thanh toán";
        return new PurchaseOrderAdminDto(
                purchaseOrder.getId(),
                purchaseOrder.getSupplierId(),
                taxId,
                supplierName,
                purchaseOrder.getDeliveryTime().format(formatter),
                status,
                list
        );
    }
}
