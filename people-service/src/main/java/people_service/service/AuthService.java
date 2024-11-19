package people_service.service;

import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.smallTrader.SmallTraderCodeDto;
import people_service.dto.smallTrader.SmallTraderForgetPasswordDto;
import people_service.dto.smallTrader.SmallTraderLocalStorageDto;
import people_service.model.AuthenticationRequest;
import people_service.model.RegistrationRequest;

public interface AuthService {
    public Long register(RegistrationRequest request);
    public SmallTraderLocalStorageDto login(AuthenticationRequest request);
    public Long checkCodeEmail(SmallTraderCodeDto smallTraderCodeDto);
    public String sendCodeToEmail(String email);
    public Long changeForgetPassword(SmallTraderForgetPasswordDto smallTraderForgetPasswordDto);

}
