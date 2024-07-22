package product_service.dto.product;

import product_service.model.Product;

import java.text.DecimalFormat;

public record ProductAdminDto (
        Long id,
        String name,
        String description,
        String price,
        String quantity,
        String imagePath
) {
    public static ProductAdminDto fromProduct(Product product) {
        DecimalFormat df = new DecimalFormat("#.000");
        String price = df.format(product.getPrice());

        return new ProductAdminDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                price,
                product.getQuantity().toString(),
                product.getImagePath()
        );
    }
}
