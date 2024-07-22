package product_service.dto.orderDetail;

public record OrderDetailAddDto (
        Long productId,
        String price,
        String quantity
){
}
