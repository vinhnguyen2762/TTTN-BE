package product_service.dto.order;

import product_service.dto.orderDetail.OrderDetailAdminDto;
import product_service.model.Order;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record OrderAdminDto(
        Long id,
        String orderDate,
        String status,
        Long customerId,
        String customerName,
        Long userId,
        String userName,
        List<OrderDetailAdminDto> list
) {
    public static OrderAdminDto fromOrder(Order order, String customerName, String userName, List<OrderDetailAdminDto> list) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String status = order.getStatus().name().equals("PENDING") ? "Chờ thanh toán" : "Đã thanh toán";

        return new OrderAdminDto(
                order.getId(),
                order.getOderDate().format(formatter),
                status,
                order.getCustomerId(),
                customerName,
                order.getUserId(),
                userName,
                list
        );
    }
}
