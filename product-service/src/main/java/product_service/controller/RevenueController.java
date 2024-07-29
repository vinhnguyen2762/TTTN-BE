package product_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import product_service.dto.revenue.RevenueRequest;
import product_service.dto.revenue.RevenueResponse;
import product_service.service.RevenueService;

@RestController
@RequestMapping("/api/v1/revenue")
public class RevenueController {
    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @PostMapping("/get-revenue")
    public ResponseEntity<RevenueResponse> getTop5Revenue(@RequestBody RevenueRequest revenueRequest) {
        RevenueResponse rs = revenueService.findTop5ProductsByRevenue(revenueRequest);
        return ResponseEntity.ok().body(rs);
    }
}
