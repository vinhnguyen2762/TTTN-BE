package product_service.dto.order;

import product_service.dto.orderDetail.OrderDetailAdminDto;
import product_service.model.Order;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public record OrderAdminDto(
        Long id,
        String orderDate,
        String status,
        Long customerId,
        String customerName,
        Long userId,
        String userName,
        String total,
        List<OrderDetailAdminDto> list
) {
    public static OrderAdminDto fromOrder(Order order, String customerName, String userName, List<OrderDetailAdminDto> list) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String status = order.getStatus().name().equals("PENDING") ? "Chờ thanh toán" : "Đã thanh toán";

        Long total = list.stream().mapToLong(od -> {
                    Long price = Long.parseLong(od.price());
                    Integer quantity = Integer.parseInt(od.quantity());
                    return price * quantity;
                }).sum();

        return new OrderAdminDto(
                order.getId(),
                order.getOderDate().format(formatter),
                status,
                order.getCustomerId(),
                customerName,
                order.getUserId(),
                userName,
                total.toString(),
                list
        );
    }
}
