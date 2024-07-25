package product_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product_service.dto.order.OrderAddDto;
import product_service.dto.order.OrderAdminDto;
import product_service.dto.order.OrderUpdateDto;
import product_service.dto.promotion.PromotionAdminDto;
import product_service.dto.promotion.PromotionPostDto;
import product_service.service.PromotionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotion")
public class PromotionController {
    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<PromotionAdminDto>> getAllPromotionAdmin() {
        List<PromotionAdminDto> list = promotionService.getAllPromotionAdmin();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/add")
    public ResponseEntity<PromotionAdminDto> addPromotion(@RequestBody PromotionPostDto promotionPostDto) {
        PromotionAdminDto promotionAdminDto = promotionService.addPromotion(promotionPostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(promotionAdminDto);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<PromotionAdminDto> updateOrder(@PathVariable Long id, @RequestBody PromotionPostDto promotionPostDto) {
        PromotionAdminDto promotionAdminDto = promotionService.updatePromotion(id, promotionPostDto);
        return ResponseEntity.ok().body(promotionAdminDto);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<Long> deleteOrder(@PathVariable Long id) {
        Long deleteId = promotionService.deletePromotion(id);
        return ResponseEntity.ok().body(deleteId);
    }

    @PostMapping ("/active/{id}")
    public ResponseEntity<Long> activePromotion(@PathVariable Long id) {
        Long activeId = promotionService.activePromotion(id);
        return ResponseEntity.ok().body(activeId);
    }
}
