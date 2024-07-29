package product_service.dto.revenue;

import java.util.List;

public record RevenueResponse(
        Integer month,
        Integer year,
        Long revenue,
        Long purchaseMoney,
        List<RevenueProduct> list
) {
}
