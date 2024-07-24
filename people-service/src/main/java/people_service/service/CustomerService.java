package people_service.service;

import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerSearchDto;
import people_service.dto.customer.CustomerUpdateDto;

import java.util.List;

public interface CustomerService {
    public List<CustomerAdminDto> getAllCustomerAdmin();
    public CustomerAdminDto addCustomer(CustomerAddDto customerAddDto);
    public CustomerAdminDto updateCustomer(Long id, CustomerUpdateDto customerUpdateDto);
    public CustomerAdminDto deleteCustomer(Long id);
    public CustomerAdminDto findById(Long id);
    public CustomerSearchDto findByPhoneNumber(String phoneNumber);
}
