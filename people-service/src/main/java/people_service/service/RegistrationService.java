package people_service.service;

import people_service.dto.customer.CustomerAddDto;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.model.RegistrationRequest;

public interface RegistrationService {
    public Long register(RegistrationRequest request);
    public Long registerCustomer(RegistrationRequest request);
    public String confirmToken(String confirmToken);
    public String confirmTokenCustomer(String confirmToken);
}
