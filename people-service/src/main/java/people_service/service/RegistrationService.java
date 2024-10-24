package people_service.service;

import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.model.RegistrationRequest;

public interface RegistrationService {
    public SmallTraderAdminDto register(RegistrationRequest request);
    public String confirmToken(String confirmToken);
}
