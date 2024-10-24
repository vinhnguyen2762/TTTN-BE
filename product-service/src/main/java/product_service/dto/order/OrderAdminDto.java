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
        Long smallTraderId,
        String smallTraderName,
        String total,
        String phoneNumber,
        List<OrderDetailAdminDto> list
) {
    public static OrderAdminDto fromOrder(Order order, String customerName, String smallTraderName, String phoneNumber, List<OrderDetailAdminDto> list) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String status = order.getStatus().name().equals("PENDING") ? "Chờ thanh toán" : "Đã thanh toán";

        Long total = list.stream().mapToLong(od -> {
                    Long price = Long.parseLong(od.price());
                    Integer quantity = Integer.parseInt(od.quantity());
                    return price * quantity;
                }).sum();

        return new OrderAdminDto(
                order.getId(),
                order.getOrderDate().format(formatter),
                status,
                order.getCustomerId(),
                customerName,
                order.getSmallTraderId(),
                smallTraderName,
                total.toString(),
                phoneNumber,
                list
        );
    }
}
