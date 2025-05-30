package people_service.service;

import people_service.dto.changePasswordDto.ChangPasswordDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.email.EmailDto;
import people_service.dto.smallTrader.SmallTraderCodeDto;
import people_service.dto.smallTrader.SmallTraderForgetPasswordDto;
import people_service.dto.smallTrader.SmallTraderLocalStorageDto;
import people_service.dto.token.TokenDto;
import people_service.model.AuthenticationRequest;
import people_service.model.RegistrationRequest;

public interface AuthService {
    public Long register(RegistrationRequest request);
    public SmallTraderLocalStorageDto login(AuthenticationRequest request);
    public Long checkCodeEmail(SmallTraderCodeDto smallTraderCodeDto);
    public Long checkEmail(EmailDto email);
    public String sendCodeToEmail(EmailDto email);
    public Long changeForgetPassword(ChangPasswordDto smallTraderForgetPasswordDto);
    public Long confirmPassword(SmallTraderForgetPasswordDto smallTraderForgetPasswordDto);
    public Long confirmPasswordEmployee(SmallTraderForgetPasswordDto smallTraderForgetPasswordDto);
    public Long checkJWT(TokenDto tokenDto);
}
