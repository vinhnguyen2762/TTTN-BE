package people_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.dto.smallTrader.SmallTraderLocalStorageDto;
import people_service.exception.AcceptedException;
import people_service.exception.AccountLockedException;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.AuthenticationRequest;
import people_service.model.Customer;
import people_service.model.SmallTrader;
import people_service.model.RegistrationRequest;
import people_service.repository.CustomerRepository;
import people_service.repository.SmallTraderRepository;
import people_service.service.AuthService;
import people_service.service.RegistrationService;
import people_service.utils.Constants;

import static people_service.utils.PasswordHashing.checkPassword;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegistrationService registrationService;
    private final SmallTraderRepository smallTraderRepository;
    private final CustomerRepository customerRepository;

    public Long register(RegistrationRequest request) {
        Long rs = registrationService.register(request);
        return rs;
    }

    public Long registerCustomer(RegistrationRequest request) {
        Long rs = registrationService.registerCustomer(request);
        return rs;
    }

    public SmallTraderLocalStorageDto login(AuthenticationRequest request) {
        boolean isSmallTrader = smallTraderRepository.findByEmail(request.getEmail()).isPresent();
        boolean isCustomer = customerRepository.findByEmail(request.getEmail()).isPresent();
        if (isSmallTrader) {
            SmallTrader smallTrader = smallTraderRepository.findByEmail(request.getEmail()).orElseThrow();
            boolean isMatch = checkPassword(request.getPassword(), smallTrader.getPassword());
            if (smallTrader.getStatus() == false) {
                throw new FailedException(String.format(Constants.ErrorMessage.USER_NOT_EXIST, request.getEmail()));
            } else if (smallTrader.getLocked() == true) {
                throw new AccountLockedException(String.format(Constants.ErrorMessage.ACCOUNT_IS_LOCKED, request.getEmail()));
            } else if (!isMatch) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
            }
            return SmallTraderLocalStorageDto.fromSmallTrader(smallTrader);
        } else if (isCustomer) {
            Customer customer = customerRepository.findByEmail(request.getEmail()).orElseThrow();
            boolean isMatch = checkPassword(request.getPassword(), customer.getPassword());
            if (customer.getLocked() == true) {
                throw new AccountLockedException(String.format(Constants.ErrorMessage.ACCOUNT_IS_LOCKED, request.getEmail()));
            } else if (!isMatch) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
            }
            return new SmallTraderLocalStorageDto(customer.getId(), "CUSTOMER");
        } else {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
        }
    }

    public CustomerAdminDto checkEmailCustomer(String email) {
        boolean isEmailExistsSmallTrader = smallTraderRepository.findByEmail(email).isPresent();
        if (isEmailExistsSmallTrader) {
            throw new FailedException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, email));
        }

        boolean isEmailExistsCustomer = customerRepository.findByEmail(email).isPresent();
        if (isEmailExistsCustomer) {
            Customer customer = customerRepository.findByEmail(email).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND_EMAIL, email)));
            if (customer.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.CUSTOMER_NOT_FOUND_EMAIL, email));
            } else {
                return CustomerAdminDto.fromCustomer(customer);
            }
        } else {
            throw new AcceptedException(String.format(Constants.ErrorMessage.EMAIL_ACCEPTED, email));
        }
    }
}
