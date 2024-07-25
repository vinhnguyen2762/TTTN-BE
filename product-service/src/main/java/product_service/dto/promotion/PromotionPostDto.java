package product_service.dto.promotion;

import product_service.dto.promotionProduct.PromotionProductPostDto;

import java.util.List;

public record PromotionPostDto(
        String name,
        String description,
        String type,
        String value,
        String startDate,
        String endDate,
        List<PromotionProductPostDto> list
) {
}
