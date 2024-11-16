package people_service.service.impl;


import org.springframework.stereotype.Service;
import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerSearchDto;
import people_service.dto.customer.CustomerUpdateDto;
import people_service.enums.Gender;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.ConfirmationTokenCustomer;
import people_service.model.Customer;
import people_service.model.SmallTrader;
import people_service.repository.CustomerRepository;
import people_service.repository.SmallTraderRepository;
import people_service.service.ConfirmationTokenCustomerService;
import people_service.service.CustomerService;
import people_service.exception.DuplicateException;
import people_service.service.client.ProductFeignClient;
import people_service.utils.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static people_service.utils.PasswordHashing.hashPassword;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ProductFeignClient productFeignClient;
    private final SmallTraderRepository smallTraderRepository;
    private final ConfirmationTokenCustomerService confirmationTokenCustomerService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ProductFeignClient productFeignClient, SmallTraderRepository smallTraderRepository, ConfirmationTokenCustomerService confirmationTokenCustomerService) {
        this.customerRepository = customerRepository;
        this.productFeignClient = productFeignClient;
        this.smallTraderRepository = smallTraderRepository;
        this.confirmationTokenCustomerService = confirmationTokenCustomerService;
    }

    public String signUpCustomer(Customer customer) {
        // check if email is taken
        boolean isEmailExists = customerRepository.findByEmail(customer.getEmail()).isPresent();
        if (isEmailExists) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, customer.getEmail()));
        }

        // check if phone number is taken
        boolean isPhoneNumberExists = customerRepository.findByPhoneNumber(customer.getPhoneNumber()).isPresent();
        if (isPhoneNumberExists) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, customer.getPhoneNumber()));
        }
        String hashedPassword = hashPassword(customer.getPassword());
        customer.setPassword(hashedPassword);

        customerRepository.saveAndFlush(customer);

        //create a random token
        String confirmToken = UUID.randomUUID().toString();
        ConfirmationTokenCustomer confirmationToken = new ConfirmationTokenCustomer(
                confirmToken,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                customer);

        //save it to database
        confirmationTokenCustomerService.saveConfirmationToken(confirmationToken);

        return confirmToken;
    }

    public void enableAppUser(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND, email)));
        customer.setLocked(false);
        customerRepository.save(customer);
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
        boolean isEmailExistsSmallTrader = smallTraderRepository.findByEmail(customerAddDto.email()).isPresent();
        if (isEmailExistsCustomer || isEmailExistsSmallTrader) {
            throw new FailedException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, customerAddDto.email()));
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
                customerAddDto.email(),
                customerAddDto.smallTraderId());
        customerRepository.saveAndFlush(customerAdd);
        return customerAdd.getId();
    }

    public Long updateCustomer(Long id, CustomerUpdateDto customerUpdateDto) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id)));
        if (customer.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id));
        }

        LocalDate date = LocalDate.parse(customerUpdateDto.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
        Gender gender = customerUpdateDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;

        String oldPhoneNumber = customer.getPhoneNumber();
        String oldEmail = customer.getEmail();

        // if phone number is new, check if the new phone number exist
        if (!customerUpdateDto.phoneNumber().equals(oldPhoneNumber)) {
            Boolean isPhoneNumberExist = customerRepository.findByPhoneNumber(customerUpdateDto.phoneNumber()).isPresent();
            if (!isPhoneNumberExist) {
                customer.setPhoneNumber(customerUpdateDto.phoneNumber());
            } else {
                throw new FailedException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, customerUpdateDto.phoneNumber()));
            }
        }

        // if email is new, check if the new email exist
        if (!customerUpdateDto.email().equals(oldEmail)) {
            boolean isEmailExistsCustomer = customerRepository.findByEmail(customerUpdateDto.email()).isPresent();
            boolean isEmailExistsSmallTrader = smallTraderRepository.findByEmail(customerUpdateDto.email()).isPresent();
            if (!isEmailExistsCustomer && !isEmailExistsSmallTrader) {
                customer.setEmail(customerUpdateDto.email());
            } else {
                throw new DuplicateException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, customerUpdateDto.email()));
            }
        }

        customer.setFirstName(customerUpdateDto.firstName());
        customer.setLastName(customerUpdateDto.lastName());
        customer.setDateOfBirth(date);
        customer.setGender(gender);
        customer.setAddress(customerUpdateDto.address());
        customerRepository.saveAndFlush(customer);
        return customer.getId();
    }

    public Long deleteCustomer(Long id) {
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
        return customer.getId();
    }

    public CustomerAdminDto findById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND, id)));
        SmallTrader smallTrader = smallTraderRepository.findById(customer.getSmallTraderId()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, customer.getSmallTraderId()))
        );
        String smallTraderName = smallTrader.getFirstName() + " " + smallTrader.getLastName();
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

    private Boolean checkCustomerHasOrder(Long id) {
        Boolean rs = productFeignClient.checkCustomerHasOrder(id).getBody();
        return rs;
    }

}
