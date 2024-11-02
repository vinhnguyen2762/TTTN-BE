package product_service.dto.statistics;

public record StatisticsResponse(
        Long productNumber,
        Long orderNumber,
        Long customerNumber,
        Long purchaseOrderNumber,
        Long supplierNumber,
        Long promotionNumber
) {
}
