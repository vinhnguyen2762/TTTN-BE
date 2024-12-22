package people_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.employee.EmployeeAddDto;
import people_service.dto.employee.EmployeeAdminDto;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.model.ChangePasswordRequest;
import people_service.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/get-all/{id}")
    public ResponseEntity<List<EmployeeAdminDto>> getAllEmployeeSmallTraderId(@PathVariable Long id) {
        List<EmployeeAdminDto> list = employeeService.getAllEmployeeSmallTrader(id);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/add")
    public ResponseEntity<Long> addEmployee(@RequestBody EmployeeAddDto employeeAddDto) {
        Long rs = employeeService.addEmployee(employeeAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<Long> updateEmployee(@PathVariable Long id, @RequestBody EmployeeAddDto employeeAddDto) {
        Long rs = employeeService.updateEmployee(id, employeeAddDto);
        return ResponseEntity.ok().body(rs);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<Long> deleteCustomer(@PathVariable Long id) {
        Long rs = employeeService.deleteEmployee(id);
        return ResponseEntity.ok().body(rs);
    }

    @PutMapping("/change-account-status/{id}")
    public ResponseEntity<Long> changeEmployeeAccountStatus(@PathVariable Long id) {
        Long rs = employeeService.changeAccountStatus(id);
        return ResponseEntity.ok().body(rs);
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<Long> changeAccountPassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        Long returnId = employeeService.changePassword(id, request);
        return ResponseEntity.ok().body(returnId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeAdminDto> getById(@PathVariable Long id) {
        EmployeeAdminDto rs = employeeService.findById(id);
        return ResponseEntity.ok().body(rs);
    }
}
