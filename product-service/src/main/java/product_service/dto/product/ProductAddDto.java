package product_service.dto.product;

public record ProductAddDto(
        String name,
        String description,
        String price,
        String quantity
) {
}
