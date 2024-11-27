package product_service.dto.promotionDetail;

import product_service.model.PromotionDetail;

public record PromotionDetailAdminDto(
        Long productId,
        String productName,
        String originalPrice,
        String discountedPrice
) {
    public static PromotionDetailAdminDto fromPromotionProduct(
            PromotionDetail promotionDetail, String productName, String originalPrice, String discountedPrice) {
        return new PromotionDetailAdminDto(
                promotionDetail.getProduct().getId(),
                productName,
                originalPrice,
                discountedPrice
        );
    }
}
