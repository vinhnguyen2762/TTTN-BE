package product_service.dto.statistics;

public record StatisticsResponse(
        Long employeeNumber,
        Long productNumber,
        Long orderNumber,
        Long customerNumber
) {
}
