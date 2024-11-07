package product_service.dto.promotion;

import product_service.dto.promotionDetail.PromotionDetailAdminDto;
import product_service.model.Promotion;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record PromotionAdminDto(
        Long id,
        String name,
        String description,
        String type,
        String value,
        String startDate,
        String endDate,
        String status,
        List<PromotionDetailAdminDto> list,
        Long smallTraderId
) {
    public static PromotionAdminDto fromPromotion(Promotion promotion, List<PromotionDetailAdminDto> list) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String status = promotion.getStatus().name().equals("PENDING") ? "Chờ áp dụng" : "Đang áp dụng";
        String type = promotion.getType().name().equals("PERCENTAGE") ? "Phần trăm" : "Số tiền cố định";

        return new PromotionAdminDto(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                type,
                promotion.getValue().toString(),
                promotion.getStartDate().format(formatter),
                promotion.getEndDate().format(formatter),
                status,
                list,
                promotion.getSmallTraderId()
        );
    }
}
