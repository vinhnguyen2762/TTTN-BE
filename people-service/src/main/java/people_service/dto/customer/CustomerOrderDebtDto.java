package people_service.dto.customer;

public record CustomerOrderDebtDto(
        Long id,
        String orderDate,
        String total
) {
}
