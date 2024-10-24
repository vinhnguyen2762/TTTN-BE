package people_service.service;

import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.model.AuthenticationRequest;
import people_service.model.RegistrationRequest;

public interface AuthService {
    public SmallTraderAdminDto register(RegistrationRequest request);
    public SmallTraderAdminDto login(AuthenticationRequest request);
}
