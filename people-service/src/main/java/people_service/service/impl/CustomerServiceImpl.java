package people_service.service.impl;


import org.springframework.stereotype.Service;
import people_service.dto.customer.*;
import people_service.enums.Gender;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.Customer;
import people_service.model.SmallTrader;
import people_service.repository.CustomerRepository;
import people_service.repository.SmallTraderRepository;
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
    private final SmallTraderRepository smallTraderRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, ProductFeignClient productFeignClient, SmallTraderRepository smallTraderRepository) {
        this.customerRepository = customerRepository;
        this.productFeignClient = productFeignClient;
        this.smallTraderRepository = smallTraderRepository;
    }

    public List<CustomerAdminDto> getAllCustomerAdmin() {
        List<Customer> list = customerRepository.findAll();
        return list.stream().map(CustomerAdminDto::fromCustomer).toList();
    }

    public List<CustomerAdminDto> getAllCustomerSmallTrader(Long id) {
        List<Customer> list = customerRepository.findBySmallTraderId(id);
        return list.stream().map(CustomerAdminDto::fromCustomer).toList();
    }

    public Long addCustomer(CustomerAddDto customerAddDto) {
        boolean isPhoneNumberExist = customerRepository.findByPhoneNumber(customerAddDto.phoneNumber()).isPresent();
        if (isPhoneNumberExist) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, customerAddDto.phoneNumber()));
        }

        boolean isEmailExistsCustomer = customerRepository.findByEmail(customerAddDto.email()).isPresent();
        if (isEmailExistsCustomer) {
            throw new FailedException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, customerAddDto.email()));
        }

        LocalDate date = LocalDate.parse(customerAddDto.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
        Gender gender = customerAddDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;
        SmallTrader smallTrader = smallTraderRepository.findById(customerAddDto.smallTraderId()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, customerAddDto.smallTraderId())));

        Customer customerAdd = new Customer(
                customerAddDto.firstName(),
                customerAddDto.lastName(),
                date,
                gender,
                customerAddDto.address(),
                customerAddDto.phoneNumber(),
                customerAddDto.email(),
                smallTrader);
        customerRepository.saveAndFlush(customerAdd);
        return customerAdd.getId();
    }

    public Long updateCustomer(Long id, CustomerAddDto customerAddDto) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id)));
        if (customer.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id));
        }

        LocalDate date = LocalDate.parse(customerAddDto.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
        Gender gender = customerAddDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;

        String oldPhoneNumber = customer.getPhoneNumber();
        String oldEmail = customer.getEmail();

        // if phone number is new, check if the new phone number exist
        if (!customerAddDto.phoneNumber().equals(oldPhoneNumber)) {
            Boolean isPhoneNumberExist = customerRepository.findByPhoneNumber(customerAddDto.phoneNumber()).isPresent();
            if (!isPhoneNumberExist) {
                customer.setPhoneNumber(customerAddDto.phoneNumber());
            } else {
                throw new FailedException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, customerAddDto.phoneNumber()));
            }
        }

        // if email is new, check if the new email exist
        if (!customerAddDto.email().equals(oldEmail)) {
            boolean isEmailExistsCustomer = customerRepository.findByEmail(customerAddDto.email()).isPresent();
            if (!isEmailExistsCustomer) {
                customer.setEmail(customerAddDto.email());
            } else {
                throw new DuplicateException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, customerAddDto.email()));
            }
        }

        customer.setFirstName(customerAddDto.firstName());
        customer.setLastName(customerAddDto.lastName());
        customer.setDateOfBirth(date);
        customer.setGender(gender);
        customer.setAddress(customerAddDto.address());
        customerRepository.saveAndFlush(customer);
        return customer.getId();
    }

    public Long deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id)));
        if (customer.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id));
        }

        List<CustomerOrderDebtDto> rs = getCustomerOrderDebt(customer.getId());

        if (!rs.isEmpty()) {
            throw new FailedException(String.format(Constants.ErrorMessage.CUSTOMER_CANT_DELETE, id));
        }

        if (checkCustomerHasOrder(id)) {
            customer.setStatus(false);
            customerRepository.saveAndFlush(customer);
        }
        else {
            customerRepository.delete(customer);
        }
        return customer.getId();
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

    public Long countCustomerSmallTrader(Long id) {
        Long rs = customerRepository.countCustomerSmallTrader(id);
        return rs;
    }


    public List<CustomerDebtDto> getAllCustomerDebt(Long id) {
        List<Customer> list = customerRepository.findBySmallTraderId(id);
        return list.stream().map(c -> {
            List<CustomerOrderDebtDto> rs = getCustomerOrderDebt(c.getId());
            return CustomerDebtDto.fromCustomer(c, rs);
        }).toList();
    }

    private Boolean checkCustomerHasOrder(Long id) {
        Boolean rs = productFeignClient.checkCustomerHasOrder(id).getBody();
        return rs;
    }

    private List<CustomerOrderDebtDto> getCustomerOrderDebt(Long id) {
        List<CustomerOrderDebtDto> rs = productFeignClient.getCustomerOrderDebt(id).getBody();
        return rs;
    }

}
