package people_service.service;

import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.dto.smallTrader.SmallTraderLocalStorageDto;
import people_service.model.AuthenticationRequest;
import people_service.model.RegistrationRequest;

public interface AuthService {
    public Long register(RegistrationRequest request);
    public Long registerCustomer(RegistrationRequest request);
    public SmallTraderLocalStorageDto login(AuthenticationRequest request);
    public CustomerAdminDto checkEmailCustomer(String email);
    public String sendCodeToEmail(String email);

}
