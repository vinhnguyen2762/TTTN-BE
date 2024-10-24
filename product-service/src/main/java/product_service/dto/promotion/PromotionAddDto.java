package product_service.dto.promotion;

import product_service.dto.promotionDetail.PromotionDetailPostDto;

import java.util.List;

public record PromotionAddDto(
        String name,
        String description,
        String type,
        String value,
        String startDate,
        String endDate,
        List<PromotionDetailPostDto> list,
        Long smallTraderId
) {
}
