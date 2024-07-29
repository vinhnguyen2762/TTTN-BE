package product_service.service;

import product_service.dto.revenue.RevenueRequest;
import product_service.dto.revenue.RevenueResponse;

public interface RevenueService {
    public RevenueResponse findTop5ProductsByRevenue(RevenueRequest revenueRequest);
}
