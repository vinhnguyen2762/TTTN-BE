package product_service.service;

import product_service.dto.purchaseOrder.PurchaseOrderAdminDto;
import product_service.dto.purchaseOrder.PurchaseOrderAddDto;

import java.util.List;

public interface PurchaseOrderService {
    public List<PurchaseOrderAdminDto> getAllSupplyDetailAdmin();
    public Long addSupplyDetail(PurchaseOrderAddDto purchaseOrderAddDto);
    public Long updateSupplyDetail(Long id, PurchaseOrderAddDto purchaseOrderAddDto);
    public Long deleteSupplyDetail(Long id);
    public Long paySupplyDetail(Long id);
}
