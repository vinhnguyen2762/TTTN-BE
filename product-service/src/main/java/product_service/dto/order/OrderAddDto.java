package product_service.dto.order;

import product_service.dto.orderDetail.OrderDetailPostDto;

import java.util.List;

public record OrderAddDto(
        Long customerId,
        Long smallTraderId,
        List<OrderDetailPostDto> list
) {
}
