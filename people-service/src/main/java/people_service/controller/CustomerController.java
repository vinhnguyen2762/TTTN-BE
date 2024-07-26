package people_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerSearchDto;
import people_service.dto.customer.CustomerUpdateDto;
import people_service.model.Customer;
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

    @PostMapping("/add")
    public ResponseEntity<CustomerAdminDto> addCustomer(@RequestBody CustomerAddDto customerAddDto) {
        CustomerAdminDto customerAdminDto = customerService.addCustomer(customerAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerAdminDto);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<CustomerAdminDto> updateCustomer(@PathVariable Long id, @RequestBody CustomerUpdateDto customerUpdateDto) {
        CustomerAdminDto customerAdminDto = customerService.updateCustomer(id, customerUpdateDto);
        return ResponseEntity.ok().body(customerAdminDto);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<CustomerAdminDto> deleteCustomer(@PathVariable Long id) {
        CustomerAdminDto customerAdminDto = customerService.deleteCustomer(id);
        return ResponseEntity.ok().body(customerAdminDto);
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
}
