package product_service.dto.product;

public record ProductUpdateDto(
        String name,
        String description,
        String price,
        String quantity
) {
}
