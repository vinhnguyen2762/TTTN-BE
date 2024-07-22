package people_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import people_service.dto.employee.EmployeeAdminDto;
import people_service.exception.AccountLockedException;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.AuthenticationRequest;
import people_service.model.Employee;
import people_service.model.RegistrationRequest;
import people_service.repository.EmployeeRepository;
import people_service.service.AuthService;
import people_service.service.RegistrationService;
import people_service.utils.Constants;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegistrationService registrationService;
    private final EmployeeRepository employeeRepository;

    public EmployeeAdminDto register(RegistrationRequest request) {
        EmployeeAdminDto employeeAdminDto = registrationService.register(request);
        return employeeAdminDto;
    }

    public EmployeeAdminDto login(AuthenticationRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND, request.getEmail())));
        if (employee.getStatus() == false) {
            throw new FailedException(String.format(Constants.ErrorMessage.USER_NOT_EXIST, request.getEmail()));
        } else if (employee.getLocked() == true) {
            throw new AccountLockedException(String.format(Constants.ErrorMessage.ACCOUNT_IS_LOCKED, request.getEmail()));
        } else if (!employee.getPassword().equals(request.getPassword())) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
        }
        return EmployeeAdminDto.fromEmployee(employee);
    }
}
