package people_service.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:9002/api/v1")
public interface ProductFeignClient {
    @GetMapping("/order/customer/{id}")
    ResponseEntity<Boolean> checkCustomerHasOrder(@PathVariable Long id);

    @GetMapping("/purchase-order/supplier/{id}")
    ResponseEntity<Boolean> checkSupplierHasPurchaseOrder(@PathVariable Long id);
}
