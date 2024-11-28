package people_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerSearchDto;
import people_service.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CustomerAdminDto>> getAllCustomerAdmin() {
        List<CustomerAdminDto> list = customerService.getAllCustomerAdmin();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/get-all/{id}")
    public ResponseEntity<List<CustomerAdminDto>> getAllCustomerAdmin(@PathVariable Long id) {
        List<CustomerAdminDto> list = customerService.getAllCustomerSmallTrader(id);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/add")
    public ResponseEntity<Long> addCustomer(@RequestBody CustomerAddDto customerAddDto) {
        Long rs = customerService.addCustomer(customerAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<Long> updateCustomer(@PathVariable Long id, @RequestBody CustomerAddDto customerAddDto) {
        Long rs = customerService.updateCustomer(id, customerAddDto);
        return ResponseEntity.ok().body(rs);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<Long> deleteCustomer(@PathVariable Long id) {
        Long rs = customerService.deleteCustomer(id);
        return ResponseEntity.ok().body(rs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerAdminDto> getById(@PathVariable Long id) {
        CustomerAdminDto customerAdminDto = customerService.findById(id);
        return ResponseEntity.ok().body(customerAdminDto);
    }

    @GetMapping("/search")
    public ResponseEntity<CustomerSearchDto> searchByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {
        CustomerSearchDto customerSearchDto = customerService.findByPhoneNumberSearch(phoneNumber);
        return ResponseEntity.ok().body(customerSearchDto);
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<Long> countCustomerByStatusTrue(@PathVariable Long id) {
        Long rs = customerService.countCustomerSmallTrader(id);
        return ResponseEntity.ok().body(rs);
    }
}
