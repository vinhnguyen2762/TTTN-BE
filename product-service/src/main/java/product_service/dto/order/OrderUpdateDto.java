package product_service.dto.order;

import product_service.dto.orderDetail.OrderDetailPostDto;

import java.util.List;

public record OrderUpdateDto(
        List<OrderDetailPostDto> list
) {
}
