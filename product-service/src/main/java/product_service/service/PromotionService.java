package product_service.service;

import product_service.dto.promotion.PromotionAddDto;
import product_service.dto.promotion.PromotionAdminDto;

import java.util.List;

public interface PromotionService {
    public List<PromotionAdminDto> getAllPromotionAdmin();
    public Long addPromotion(PromotionAddDto promotionPostDto);
    public Long updatePromotion(Long id, PromotionAddDto promotionPostDto);
    public Long deletePromotion(Long id);
    public Long activePromotion(Long id);
    public List<PromotionAdminDto> getAllPromotionSmallTrader(Long id);
}
