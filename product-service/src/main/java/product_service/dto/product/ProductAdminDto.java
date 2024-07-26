package product_service.dto.product;

import product_service.model.Product;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public record ProductAdminDto (
        Long id,
        String name,
        String description,
        String originalPrice,
        String quantity,
        String promotionName,
        String discountedPrice,
        String imagePath
) {
    public static ProductAdminDto fromProduct(Product product, String promotionName, String discountedPrice) {

        return new ProductAdminDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice().toString(),
                product.getQuantity().toString(),
                promotionName,
                discountedPrice,
                product.getImagePath()
        );
    }
}
