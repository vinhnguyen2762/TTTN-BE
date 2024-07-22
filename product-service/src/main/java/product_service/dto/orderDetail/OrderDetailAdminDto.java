package product_service.dto.orderDetail;

import product_service.model.OrderDetail;

import java.text.DecimalFormat;

public record OrderDetailAdminDto (
        Long id,
        Long productId,
        String productName,
        String price,
        String quantity
        // to do thêm tổng tiền ?
) {
    public static OrderDetailAdminDto fromOderDetail(OrderDetail orderDetail, String productName) {
        DecimalFormat df = new DecimalFormat("#.000");
        String price = df.format(orderDetail.getPrice());

        return new OrderDetailAdminDto(
                orderDetail.getId(),
                orderDetail.getProductId(),
                productName,
                price,
                orderDetail.getQuantity().toString()
        );
    }
}
