package people_service.service;

import people_service.dto.employee.EmployeeAdminDto;
import people_service.model.RegistrationRequest;

public interface RegistrationService {
    public EmployeeAdminDto register(RegistrationRequest request);
    public String confirmToken(String confirmToken);
}
