package people_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import people_service.dto.employee.EmployeeAdminDto;
import people_service.dto.employee.EmployeeUpdateDto;
import people_service.enums.Gender;
import people_service.exception.AccountLockedException;
import people_service.exception.DuplicateException;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.ChangePasswordRequest;
import people_service.model.ConfirmationToken;
import people_service.model.Employee;
import people_service.repository.EmployeeRepository;
import people_service.service.ConfirmationTokenService;
import people_service.service.EmployeeService;
import people_service.utils.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ConfirmationTokenService confirmationTokenService;

    public String signUpUser(Employee employee) {
        boolean isUserExists = employeeRepository.findByEmail(employee.getEmail()).isPresent();
        if (isUserExists) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, employee.getEmail()));
        }

        employeeRepository.saveAndFlush(employee);

        //create a random token
        String confirmToken = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                confirmToken,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                employee);

        //save it to database
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return confirmToken;
    }

    public void enableAppUser(String email) {
        Employee employee = employeeRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND, email)));
        employee.setStatus(true);
        employeeRepository.save(employee);
    }


    public List<EmployeeAdminDto> getAllEmployeeAdmin() {
        List<Employee> list = employeeRepository.findAll();
        return list.stream().map(EmployeeAdminDto::fromEmployee).toList();
    }

    public EmployeeAdminDto updateEmployee(Long id, EmployeeUpdateDto employeeUpdateDto) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id)));
        if (employee.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id));
        }

        LocalDate date = LocalDate.parse(employeeUpdateDto.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
        Gender gender = employeeUpdateDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;

        employee.setFirstName(employeeUpdateDto.firstName());
        employee.setLastName(employeeUpdateDto.lastName());
        employee.setDateOfBirth(date);
        employee.setGender(gender);
        employee.setAddress(employeeUpdateDto.address());
        employee.setPhoneNumber(employeeUpdateDto.phoneNumber());
        employeeRepository.saveAndFlush(employee);
        return EmployeeAdminDto.fromEmployee(employee);
    }

    public EmployeeAdminDto deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id)));
        if (employee.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id));
        }
        employee.setStatus(false);
        employeeRepository.saveAndFlush(employee);
        return EmployeeAdminDto.fromEmployee(employee);
    }

    public EmployeeAdminDto changeAccountStatus(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id)));
        employee.setLocked(!employee.getLocked());
        employeeRepository.saveAndFlush(employee);
        return EmployeeAdminDto.fromEmployee(employee);
    }

    public Long changePassword(Long id, ChangePasswordRequest changePasswordRequest) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND_ID, id)));
        if (!employee.getPassword().equals(changePasswordRequest.getOldPassword())) {
            throw new FailedException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
        } else {
            employee.setPassword(changePasswordRequest.getNewPassword());
            employeeRepository.saveAndFlush(employee);
        }
        return employee.getId();
    }

    public EmployeeAdminDto findById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id)));
        if (employee.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id));
        }
        return EmployeeAdminDto.fromEmployee(employee);
    }

    public Long countEmployeeByStatusTrue() {
        Long rs = employeeRepository.countEmployeeByStatusTrue();
        return rs;
    }
}
