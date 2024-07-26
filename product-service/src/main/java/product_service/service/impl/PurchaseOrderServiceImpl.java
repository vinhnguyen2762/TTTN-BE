package product_service.service.impl;

import org.springframework.stereotype.Service;
import product_service.dto.purchaseOrder.PurchaseOrderAdminDto;
import product_service.dto.purchaseOrder.PurchaseOrderAddDto;
import product_service.repository.ProductRepository;
import product_service.repository.PurchaseOrderDetailRepository;
import product_service.repository.PurchaseOrderRepository;
import product_service.service.PurchaseOrderService;
import product_service.service.client.PeopleFeignClient;

import java.util.List;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private final PeopleFeignClient peopleFeignClient;
    private final ProductRepository productRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, PurchaseOrderDetailRepository purchaseOrderDetailRepository, PeopleFeignClient peopleFeignClient, ProductRepository productRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderDetailRepository = purchaseOrderDetailRepository;
        this.peopleFeignClient = peopleFeignClient;
        this.productRepository = productRepository;
    }

    public List<PurchaseOrderAdminDto> getAllSupplyDetailAdmin() {
        return null;
    }

    @Override
    public Long addSupplyDetail(PurchaseOrderAddDto purchaseOrderAddDto) {
        return null;
    }

    @Override
    public Long updateSupplyDetail(Long id, PurchaseOrderAddDto purchaseOrderAddDto) {
        return null;
    }

    @Override
    public Long deleteSupplyDetail(Long id) {
        return null;
    }

    @Override
    public Long paySupplyDetail(Long id) {
        return null;
    }
}
