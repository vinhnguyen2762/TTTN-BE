package product_service.dto.promotionProduct;

import product_service.model.PromotionProduct;

public record PromotionProductAdminDto(
        Long productId,
        String productName,
        String originalPrice,
        String discountedPrice
) {
    public static PromotionProductAdminDto fromPromotionProduct(
            PromotionProduct promotionProduct, String productName, String originalPrice, String discountedPrice) {
        return new PromotionProductAdminDto(
                promotionProduct.getProductId(),
                productName,
                originalPrice,
                discountedPrice
        );
    }
}
