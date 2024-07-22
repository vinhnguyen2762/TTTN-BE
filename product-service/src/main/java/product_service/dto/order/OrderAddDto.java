package product_service.dto.order;

import product_service.dto.orderDetail.OrderDetailAddDto;

import java.util.List;

public record OrderAddDto(
        Long customerId,
        Long userId,
        List<OrderDetailAddDto> list
) {
}
