package people_service.service;

import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerSearchDto;
import people_service.dto.employee.EmployeeAddDto;
import people_service.dto.employee.EmployeeAdminDto;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.model.ChangePasswordRequest;

import java.util.List;

public interface EmployeeService {
    public List<EmployeeAdminDto> getAllEmployeeSmallTrader(Long id);
    public Long addEmployee(EmployeeAddDto employeeAddDto);
    public Long updateEmployee(Long id, EmployeeAddDto employeeAddDto);
    public Long deleteEmployee(Long id);
    public Long changePassword(Long id, ChangePasswordRequest changePasswordRequest);
    public Long changeAccountStatus(Long id);
    public EmployeeAdminDto findById(Long id);

}
