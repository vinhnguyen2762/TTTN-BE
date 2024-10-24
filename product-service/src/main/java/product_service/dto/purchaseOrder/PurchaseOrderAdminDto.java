package product_service.dto.purchaseOrder;

import product_service.dto.purchaseOrderDetail.PurchaseOrderDetailAdminDto;
import product_service.model.PurchaseOrder;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record PurchaseOrderAdminDto(
        Long id,
        Long supplierId,
        String taxId,
        String supplierName,
        String createDate,
        String deliveryDate,
        String status,
        String total,
        List<PurchaseOrderDetailAdminDto> list,
        String smallTraderName
) {
    public static PurchaseOrderAdminDto fromPurchaseOrder(PurchaseOrder purchaseOrder, String taxId, String supplierName, List<PurchaseOrderDetailAdminDto> list, String smallTraderName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String status = purchaseOrder.getStatus().name().equals("PENDING") ? "Chờ thanh toán" : "Đã thanh toán";

        Long total = list.stream().mapToLong(pod -> {
            Long supplyPrice = Long.parseLong(pod.supplyPrice());
            Integer quantity = Integer.parseInt(pod.quantity());
            return supplyPrice * quantity;
        }).sum();

        return new PurchaseOrderAdminDto(
                purchaseOrder.getId(),
                purchaseOrder.getSupplierId(),
                taxId,
                supplierName,
                purchaseOrder.getCreateDate().format(formatter),
                purchaseOrder.getDeliveryDate().format(formatter),
                status,
                total.toString(),
                list,
                smallTraderName
        );
    }
}
