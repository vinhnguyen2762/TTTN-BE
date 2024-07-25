package product_service.service;

import product_service.dto.order.OrderAddDto;
import product_service.dto.order.OrderAdminDto;
import product_service.dto.order.OrderUpdateDto;
import product_service.dto.promotion.PromotionAdminDto;
import product_service.dto.promotion.PromotionPostDto;
import product_service.model.Promotion;

import java.util.List;

public interface PromotionService {
    public List<PromotionAdminDto> getAllPromotionAdmin();
    public PromotionAdminDto addPromotion(PromotionPostDto promotionPostDto);
    public PromotionAdminDto updatePromotion(Long id, PromotionPostDto promotionPostDto);
    public Long deletePromotion(Long id);
    public Long activePromotion(Long id);
}
