package product_service.dto.product;

public record ProductUpdateDto(
        String description,
        String price,
        String quantity
) {
}
