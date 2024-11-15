package people_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import people_service.exception.NotFoundException;
import people_service.model.ConfirmationToken;
import people_service.model.ConfirmationTokenCustomer;
import people_service.repository.ConfirmationTokenCustomerRepository;
import people_service.repository.ConfirmationTokenRepository;
import people_service.service.ConfirmationTokenCustomerService;
import people_service.service.ConfirmationTokenService;
import people_service.utils.Constants;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenCustomerServiceImpl implements ConfirmationTokenCustomerService {

    private final ConfirmationTokenCustomerRepository confirmationTokenCustomerRepository;

    public void saveConfirmationToken(ConfirmationTokenCustomer token) {
        confirmationTokenCustomerRepository.save(token);
    }

    public Optional<ConfirmationTokenCustomer> getToken(String token) {
        return confirmationTokenCustomerRepository.findByToken(token);
    }

    public void setConfirmedAt(String confirmToken) {
        ConfirmationTokenCustomer tokenFound = confirmationTokenCustomerRepository.findByToken(confirmToken).orElseThrow(() -> new NotFoundException(String.format(Constants.ErrorMessage.TOKEN_NOT_FOUND, confirmToken)));
        tokenFound.setConfirmedAt(LocalDateTime.now());
        confirmationTokenCustomerRepository.save(tokenFound);
    }
}
