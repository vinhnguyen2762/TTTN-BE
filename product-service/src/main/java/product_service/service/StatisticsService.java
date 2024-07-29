package product_service.service;

import product_service.dto.revenue.RevenueRequest;
import product_service.dto.revenue.RevenueResponse;
import product_service.dto.statistics.StatisticsResponse;

public interface StatisticsService {
    public RevenueResponse findTop5ProductsByRevenue(RevenueRequest revenueRequest);
    public StatisticsResponse getDataHomePage(RevenueRequest revenueRequest);
}
