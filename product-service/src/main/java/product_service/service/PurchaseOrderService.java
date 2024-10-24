package product_service.service;

import product_service.dto.purchaseOrder.PurchaseOrderAdminDto;
import product_service.dto.purchaseOrder.PurchaseOrderAddDto;
import product_service.dto.purchaseOrder.PurchaseOrderUpdateDto;

import java.util.List;

public interface PurchaseOrderService {
    public List<PurchaseOrderAdminDto> getAllPurchaseOrderAdmin();
    public Long addPurchaseOrder(PurchaseOrderAddDto purchaseOrderAddDto);
    public Long updatePurchaseOrder(Long id, PurchaseOrderUpdateDto purchaseOrderUpdateDto);
    public Long deletePurchaseOrder(Long id);
    public Long payPurchaseOrder(Long id);
    public List<PurchaseOrderAdminDto> getAllPurchaseOrderSmallTraderId(Long id);
}
