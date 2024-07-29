package product_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import product_service.dto.revenue.RevenueRequest;
import product_service.dto.revenue.RevenueResponse;
import product_service.dto.statistics.StatisticsResponse;
import product_service.service.StatisticsService;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping("/revenue")
    public ResponseEntity<RevenueResponse> getTop5Revenue(@RequestBody RevenueRequest revenueRequest) {
        RevenueResponse rs = statisticsService.findTop5ProductsByRevenue(revenueRequest);
        return ResponseEntity.ok().body(rs);
    }

    @PostMapping("/home")
    public ResponseEntity<StatisticsResponse> getDateHomePage(@RequestBody RevenueRequest revenueRequest) {
        StatisticsResponse rs = statisticsService.getDataHomePage(revenueRequest);
        return ResponseEntity.ok().body(rs);
    }
}
