package people_service.service.impl;


import org.springframework.stereotype.Service;
import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerSearchDto;
import people_service.dto.customer.CustomerUpdateDto;
import people_service.enums.Gender;
import people_service.exception.NotFoundException;
import people_service.model.Customer;
import people_service.repository.CustomerRepository;
import people_service.service.CustomerService;
import people_service.exception.DuplicateException;
import people_service.service.client.ProductFeignClient;
import people_service.utils.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ProductFeignClient productFeignClient;

    public CustomerServiceImpl(CustomerRepository customerRepository, ProductFeignClient productFeignClient) {
        this.customerRepository = customerRepository;
        this.productFeignClient = productFeignClient;
    }

    public List<CustomerAdminDto> getAllCustomerAdmin() {
        List<Customer> list = customerRepository.findAll();
        return list.stream().map(CustomerAdminDto::fromCustomer).toList();
    }

    public CustomerAdminDto addCustomer(CustomerAddDto customerAddDto) {
        Boolean isExist = customerRepository.findByPhoneNumber(customerAddDto.phoneNumber()).isPresent();
        if (isExist) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.CUSTOMER_ALREADY_TAKEN, customerAddDto.phoneNumber()));
        }

        LocalDate date = LocalDate.parse(customerAddDto.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
        Gender gender = customerAddDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;

        Customer customerAdd = new Customer(
                customerAddDto.firstName(),
                customerAddDto.lastName(),
                date,
                gender,
                customerAddDto.address(),
                customerAddDto.phoneNumber(),
                customerAddDto.email());
        customerRepository.saveAndFlush(customerAdd);
        return CustomerAdminDto.fromCustomer(customerAdd);
    }

    public CustomerAdminDto updateCustomer(Long id, CustomerUpdateDto customerUpdateDto) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id)));
        if (customer.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id));
        }

        LocalDate date = LocalDate.parse(customerUpdateDto.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
        Gender gender = customerUpdateDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;

        String oldPhoneNumber = customer.getPhoneNumber();
        String oldEmail = customer.getEmail();

        // if email is new, check if the new email exist
        if (!customerUpdateDto.email().equals(oldEmail)) {
            Boolean isEmailExist = customerRepository.findByEmail(customerUpdateDto.email()).isPresent();
            if (!isEmailExist) {
                customer.setEmail(customerUpdateDto.email());
            } else {
                throw new DuplicateException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, customerUpdateDto.email()));
            }
        }

        // if phone number is new, check if the new phone number exist
        if (!customerUpdateDto.phoneNumber().equals(oldPhoneNumber)) {
            Boolean isPhoneNumberExist = customerRepository.findByPhoneNumber(customerUpdateDto.phoneNumber()).isPresent();
            if (!isPhoneNumberExist) {
                customer.setPhoneNumber(customerUpdateDto.phoneNumber());
            } else {
                throw new DuplicateException(String.format(Constants.ErrorMessage.CUSTOMER_ALREADY_TAKEN, customerUpdateDto.phoneNumber()));
            }
        }

        customer.setFirstName(customerUpdateDto.firstName());
        customer.setLastName(customerUpdateDto.lastName());
        customer.setDateOfBirth(date);
        customer.setGender(gender);
        customer.setAddress(customerUpdateDto.address());
        customerRepository.saveAndFlush(customer);
        return CustomerAdminDto.fromCustomer(customer);
    }

    public CustomerAdminDto deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id)));
        if (customer.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id));
        }
        if (checkCustomerHasOrder(id)) {
            customer.setStatus(false);
            customerRepository.saveAndFlush(customer);
        }
        else {
            customerRepository.delete(customer);
        }
        return CustomerAdminDto.fromCustomer(customer);
    }

    public CustomerAdminDto findById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id)));
        return CustomerAdminDto.fromCustomer(customer);
    }

    public CustomerSearchDto findByPhoneNumberSearch(String phoneNumber) {
        Customer customer = customerRepository.findByPhoneNumberSearch(phoneNumber).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND_PHONE_NUMBER, phoneNumber)));
        String fullName = customer.getFirstName() + " " + customer.getLastName();
        return new CustomerSearchDto(
                customer.getId().toString(),
                fullName
        );
    }

    public Long countCustomerByStatusTrue() {
        Long rs = customerRepository.countCustomerByStatusTrue();
        return rs;
    }

    private Boolean checkCustomerHasOrder(Long id) {
        Boolean rs = productFeignClient.checkCustomerHasOrder(id).getBody();
        return rs;
    }

}
