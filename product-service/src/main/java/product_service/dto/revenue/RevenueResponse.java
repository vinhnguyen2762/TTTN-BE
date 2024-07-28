package product_service.dto.revenue;

import java.util.List;

public record RevenueResponse(
        Integer month,
        Integer year,
        Long revenue,
        List<RevenueProduct> list
) {
}
