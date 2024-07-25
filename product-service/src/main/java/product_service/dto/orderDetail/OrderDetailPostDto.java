package product_service.dto.orderDetail;

public record OrderDetailPostDto(
        Long productId,
        String price,
        String quantity
){
}
