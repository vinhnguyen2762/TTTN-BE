package people_service.service;

import people_service.dto.employee.EmployeeAdminDto;
import people_service.dto.employee.EmployeeUpdateDto;
import people_service.model.ChangePasswordRequest;
import people_service.model.Employee;

import java.util.List;

public interface EmployeeService {
    public String signUpUser(Employee employee);
    public void enableAppUser(String email);
    public List<EmployeeAdminDto> getAllEmployeeAdmin();
    public EmployeeAdminDto updateEmployee(Long id, EmployeeUpdateDto employeeUpdateDto);
    public EmployeeAdminDto deleteEmployee(Long id);
    public EmployeeAdminDto changeAccountStatus(Long id);
    public Long changePassword(Long id, ChangePasswordRequest changePasswordRequest);
    public EmployeeAdminDto findById(Long id);
}
