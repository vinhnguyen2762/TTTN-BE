package product_service.dto.orderDetail;

import product_service.model.OrderDetail;

import java.text.NumberFormat;
import java.util.Locale;

public record OrderDetailAdminDto (
        Long id,
        Long productId,
        String productName,
        String price,
        String quantity
) {
    public static OrderDetailAdminDto fromOderDetail(OrderDetail orderDetail, String productName) {

        return new OrderDetailAdminDto(
                orderDetail.getId(),
                orderDetail.getProduct().getId(),
                productName,
                orderDetail.getPrice().toString(),
                orderDetail.getQuantity().toString()
        );
    }
}
