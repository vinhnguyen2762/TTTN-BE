package people_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerUpdateDto;
import people_service.dto.employee.EmployeeAdminDto;
import people_service.dto.employee.EmployeeUpdateDto;
import people_service.model.ChangePasswordRequest;
import people_service.model.Employee;
import people_service.service.impl.EmployeeServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;

    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<EmployeeAdminDto>> getAllEmployeeAdmin() {
        List<EmployeeAdminDto> list = employeeService.getAllEmployeeAdmin();
        return ResponseEntity.ok().body(list);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeAdminDto> updateEmployee(@PathVariable Long id, @RequestBody EmployeeUpdateDto employeeUpdateDto) {
        EmployeeAdminDto employeeAdminDto = employeeService.updateEmployee(id, employeeUpdateDto);
        return ResponseEntity.ok().body(employeeAdminDto);
    }

    @PutMapping("/change-account-status/{id}")
    public ResponseEntity<EmployeeAdminDto> changeEmployeeAccountStatus(@PathVariable Long id) {
        EmployeeAdminDto employeeAdminDto = employeeService.changeAccountStatus(id);
        return ResponseEntity.ok().body(employeeAdminDto);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<EmployeeAdminDto> deleteEmployee(@PathVariable Long id) {
        EmployeeAdminDto employeeAdminDto = employeeService.deleteEmployee(id);
        return ResponseEntity.ok().body(employeeAdminDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeAdminDto> getById(@PathVariable Long id) {
        EmployeeAdminDto employeeAdminDto = employeeService.findById(id);
        return ResponseEntity.ok().body(employeeAdminDto);
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<Long> changeAccountPassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        Long returnId = employeeService.changePassword(id, request);
        return ResponseEntity.ok().body(returnId);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countEmployeeByStatusTrue() {
        Long rs = employeeService.countEmployeeByStatusTrue();
        return ResponseEntity.ok().body(rs);
    }
}
