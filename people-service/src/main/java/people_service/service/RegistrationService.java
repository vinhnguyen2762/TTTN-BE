package people_service.service;

import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.model.RegistrationRequest;

public interface RegistrationService {
    public Long register(RegistrationRequest request);
    public String confirmToken(String confirmToken);
}
