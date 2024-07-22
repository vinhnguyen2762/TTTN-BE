package product_service.dto.order;

import product_service.dto.orderDetail.OrderDetailAddDto;

import java.util.List;

public record OrderUpdateDto(
        Long customerId,
        List<OrderDetailAddDto> list
) {
}
