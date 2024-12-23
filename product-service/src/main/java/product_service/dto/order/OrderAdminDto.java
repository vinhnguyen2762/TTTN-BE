package product_service.dto.order;

import product_service.dto.orderDetail.OrderDetailAdminDto;
import product_service.model.Order;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record OrderAdminDto(
        Long id,
        String orderDate,
        String paidDate,
        String status,
        Long customerId,
        String customerName,
        Long smallTraderId,
        String smallTraderName,
        String total,
        String phoneNumber,
        List<OrderDetailAdminDto> list,
        String smallTraderPhoneNumber
) {
    public static OrderAdminDto fromOrder(Order order, String customerName, String smallTraderName, String phoneNumber, List<OrderDetailAdminDto> list, String smallTraderPhoneNumber) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String status = order.getStatus().name().equals("PENDING") ? "Chờ thanh toán" : "Đã thanh toán";

        Long total = list.stream().mapToLong(od -> {
                    Long price = Long.parseLong(od.price());
                    Integer quantity = Integer.parseInt(od.quantity());
                    return price * quantity;
                }).sum();

        String paidDate = "---";
        if (order.getPaidDate() != null) {
            paidDate = order.getPaidDate().format(formatter);
        }

        return new OrderAdminDto(
                order.getId(),
                order.getOrderDate().format(formatter),
                paidDate,
                status,
                order.getCustomerId(),
                customerName,
                order.getSmallTraderId(),
                smallTraderName,
                total.toString(),
                phoneNumber,
                list,
                smallTraderPhoneNumber
        );
    }
}
