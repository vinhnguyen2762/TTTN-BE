package product_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product_service.dto.order.OrderAddDto;
import product_service.dto.order.OrderAdminDto;
import product_service.dto.order.OrderUpdateDto;
import product_service.dto.purchaseOrder.PurchaseOrderAddDto;
import product_service.dto.purchaseOrder.PurchaseOrderAdminDto;
import product_service.dto.purchaseOrder.PurchaseOrderUpdateDto;
import product_service.service.PurchaseOrderService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchase-order")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<PurchaseOrderAdminDto>> getAllPurchaseOrderAdmin() {
        List<PurchaseOrderAdminDto> list = purchaseOrderService.getAllPurchaseOrderAdmin();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/add")
    public ResponseEntity<PurchaseOrderAdminDto> addPurchaseOder(@RequestBody PurchaseOrderAddDto purchaseOrderAddDto) {
        PurchaseOrderAdminDto purchaseOrderAdminDto = purchaseOrderService.addPurchaseOrder(purchaseOrderAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderAdminDto);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<PurchaseOrderAdminDto> updatePurchaseOrder(@PathVariable Long id, @RequestBody PurchaseOrderUpdateDto purchaseOrderUpdateDto) {
        PurchaseOrderAdminDto purchaseOrderAdminDto = purchaseOrderService.updatePurchaseOrder(id, purchaseOrderUpdateDto);
        return ResponseEntity.ok().body(purchaseOrderAdminDto);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<Long> deletePurchaseOrder(@PathVariable Long id) {
        Long deleteId = purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.ok().body(deleteId);
    }

    @PostMapping ("/pay/{id}")
    public ResponseEntity<Long> payPurchaseOrder(@PathVariable Long id) {
        Long payId = purchaseOrderService.payPurchaseOrder(id);
        return ResponseEntity.ok().body(payId);
    }
}
