package product_service.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import product_service.dto.customer.CustomerAdminDto;
import product_service.dto.smallTrader.SmallTraderAdminDto;
import product_service.dto.supplier.SupplierAdminDto;

@FeignClient(name = "people-service", url = "http://localhost:9001/api/v1")
public interface PeopleFeignClient {

    @GetMapping("/small-trader/{id}")
    ResponseEntity<SmallTraderAdminDto> getById(@PathVariable Long id);

    @GetMapping("/small-trader/count")
    ResponseEntity<Long> countSmallTraderByStatusTrue();

    @GetMapping("/customer/{id}")
    ResponseEntity<CustomerAdminDto> getCustomerById(@PathVariable Long id);

    @GetMapping("/customer/count/{id}")
    ResponseEntity<Long> countCustomerBySmallTraderId(@PathVariable Long id);

    @GetMapping("/supplier/{id}")
    ResponseEntity<SupplierAdminDto> getSupplierById(@PathVariable Long id);

    @GetMapping("/supplier/count/{id}")
    ResponseEntity<Long> countSupplierBySmallTraderId(@PathVariable Long id);

}
