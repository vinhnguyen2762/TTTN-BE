package product_service.dto.revenue;

public record RevenueRequest(
        Integer month,
        Integer year,
        Long smallTraderId
) {
}
