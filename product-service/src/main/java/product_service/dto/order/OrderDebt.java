package product_service.dto.order;

import product_service.model.Order;

import java.time.format.DateTimeFormatter;

public record OrderDebt(
        Long id,
        String orderDate,
        String total
) {
    public static OrderDebt fromOrder(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Long total = order.getOrderDetails().stream().mapToLong(od -> {
            Long price = od.getPrice();
            Integer quantity = od.getQuantity();
            return price * quantity;
        }).sum();

        return new OrderDebt(
                order.getId(),
                order.getOrderDate().format(formatter),
                total.toString()
        );
    }
}
