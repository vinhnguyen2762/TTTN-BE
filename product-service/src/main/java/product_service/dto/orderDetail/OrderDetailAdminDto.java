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
        // to do thêm tổng tiền ?
) {
    public static OrderDetailAdminDto fromOderDetail(OrderDetail orderDetail, String productName) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
        String price = numberFormat.format(orderDetail.getPrice());

        return new OrderDetailAdminDto(
                orderDetail.getId(),
                orderDetail.getProductId(),
                productName,
                price,
                orderDetail.getQuantity().toString()
        );
    }
}
