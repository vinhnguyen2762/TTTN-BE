package product_service.dto.statistics;

public record StatisticsResponse(
        Long smallTraderNumber,
        Long productNumber,
        Long orderNumber,
        Long customerNumber
) {
}
