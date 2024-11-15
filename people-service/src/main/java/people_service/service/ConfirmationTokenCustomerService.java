package people_service.service;

import people_service.model.ConfirmationToken;
import people_service.model.ConfirmationTokenCustomer;

import java.util.Optional;

public interface ConfirmationTokenCustomerService {
    public void saveConfirmationToken(ConfirmationTokenCustomer token);
    public Optional<ConfirmationTokenCustomer> getToken(String token);
    public void setConfirmedAt(String confirmToken);
}
