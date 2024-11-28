package people_service.service;

import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerSearchDto;

import java.util.List;

public interface CustomerService {
    public List<CustomerAdminDto> getAllCustomerAdmin();
    public List<CustomerAdminDto> getAllCustomerSmallTrader(Long id);
    public Long addCustomer(CustomerAddDto customerAddDto);
    public Long updateCustomer(Long id, CustomerAddDto customerAddDto);
    public Long deleteCustomer(Long id);
    public CustomerAdminDto findById(Long id);
    public CustomerSearchDto findByPhoneNumberSearch(String phoneNumber);
    public Long countCustomerSmallTrader(Long id);
}
