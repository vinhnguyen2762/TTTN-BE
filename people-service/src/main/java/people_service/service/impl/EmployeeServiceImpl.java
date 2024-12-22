package people_service.service.impl;

import org.springframework.stereotype.Service;
import people_service.dto.employee.EmployeeAddDto;
import people_service.dto.employee.EmployeeAdminDto;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.enums.Gender;
import people_service.exception.DuplicateException;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.ChangePasswordRequest;
import people_service.model.Customer;
import people_service.model.Employee;
import people_service.model.SmallTrader;
import people_service.repository.EmployeeRepository;
import people_service.repository.SmallTraderRepository;
import people_service.service.EmployeeService;
import people_service.utils.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static people_service.utils.PasswordHashing.checkPassword;
import static people_service.utils.PasswordHashing.hashPassword;
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final SmallTraderRepository smallTraderRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SmallTraderRepository smallTraderRepository) {
        this.employeeRepository = employeeRepository;
        this.smallTraderRepository = smallTraderRepository;
    }

    public List<EmployeeAdminDto> getAllEmployeeSmallTrader(Long id) {
        List<Employee> list = employeeRepository.findBySmallTraderId(id);
        return list.stream().map(EmployeeAdminDto::fromEmployee).toList();
    }

    public Long addEmployee(EmployeeAddDto employeeAddDto) {
        boolean isPhoneNumberExist = employeeRepository.findByPhoneNumber(employeeAddDto.phoneNumber()).isPresent();
        if (isPhoneNumberExist) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, employeeAddDto.phoneNumber()));
        }

        boolean isEmailExistsCustomer = employeeRepository.findByEmail(employeeAddDto.email()).isPresent();
        boolean isExistSmallTrader = smallTraderRepository.findByEmail(employeeAddDto.email()).isPresent();
        if (isEmailExistsCustomer || isExistSmallTrader) {
            throw new FailedException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, employeeAddDto.email()));
        }

        LocalDate date = LocalDate.parse(employeeAddDto.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
        Gender gender = employeeAddDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;
        SmallTrader smallTrader = smallTraderRepository.findById(employeeAddDto.smallTraderId()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, employeeAddDto.smallTraderId())));

        Employee employeeAdd = new Employee(
                employeeAddDto.firstName(),
                employeeAddDto.lastName(),
                date,
                gender,
                employeeAddDto.address(),
                employeeAddDto.phoneNumber(),
                employeeAddDto.email(),
                smallTrader);
        employeeRepository.saveAndFlush(employeeAdd);
        return employeeAdd.getId();
    }


    public Long updateEmployee(Long id, EmployeeAddDto employeeAddDto) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id)));
        if (employee.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id));
        }

        LocalDate date = LocalDate.parse(employeeAddDto.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
        Gender gender = employeeAddDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;

        String oldPhoneNumber = employee.getPhoneNumber();
        String oldEmail = employee.getEmail();

        // if phone number is new, check if the new phone number exist
        if (!employeeAddDto.phoneNumber().equals(oldPhoneNumber)) {
            Boolean isPhoneNumberExist = employeeRepository.findByPhoneNumber(employeeAddDto.phoneNumber()).isPresent();
            if (!isPhoneNumberExist) {
                employee.setPhoneNumber(employeeAddDto.phoneNumber());
            } else {
                throw new FailedException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, employeeAddDto.phoneNumber()));
            }
        }

        // if email is new, check if the new email exist
        if (!employeeAddDto.email().equals(oldEmail)) {
            boolean isEmailExistsCustomer = employeeRepository.findByEmail(employeeAddDto.email()).isPresent();
            boolean isExistSmallTrader = smallTraderRepository.findByEmail(employeeAddDto.email()).isPresent();
            if (!isEmailExistsCustomer || isExistSmallTrader) {
                employee.setEmail(employeeAddDto.email());
            } else {
                throw new DuplicateException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, employeeAddDto.email()));
            }
        }

        employee.setFirstName(employeeAddDto.firstName());
        employee.setLastName(employeeAddDto.lastName());
        employee.setDateOfBirth(date);
        employee.setGender(gender);
        employee.setAddress(employeeAddDto.address());
        employeeRepository.saveAndFlush(employee);
        return employee.getId();
    }


    public Long deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id)));
        if (employee.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id));
        }
        employee.setStatus(false);
        employeeRepository.saveAndFlush(employee);
        return employee.getId();
    }

    public Long changePassword(Long id, ChangePasswordRequest changePasswordRequest) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id)));
        boolean isMatch = checkPassword(changePasswordRequest.getOldPassword(), employee.getPassword());
        if (!isMatch) {
            throw new FailedException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
        } else {
            employee.setPassword(hashPassword(changePasswordRequest.getNewPassword()));
            employeeRepository.saveAndFlush(employee);
        }
        return employee.getId();
    }

    public Long changeAccountStatus(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id)));
        employee.setLocked(!employee.getLocked());
        employeeRepository.saveAndFlush(employee);
        return employee.getId();
    }

    public EmployeeAdminDto findById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, id)));
        return EmployeeAdminDto.fromEmployee(employee);
    }
}
