package people_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.dto.smallTrader.SmallTraderUpdateDto;
import people_service.model.ChangePasswordRequest;
import people_service.service.impl.SmallTraderServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final SmallTraderServiceImpl employeeService;

    public EmployeeController(SmallTraderServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<SmallTraderAdminDto>> getAllEmployeeAdmin() {
        List<SmallTraderAdminDto> list = employeeService.getAllEmployeeAdmin();
        return ResponseEntity.ok().body(list);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SmallTraderAdminDto> updateEmployee(@PathVariable Long id, @RequestBody SmallTraderUpdateDto smallTraderUpdateDto) {
        SmallTraderAdminDto smallTraderAdminDto = employeeService.updateEmployee(id, smallTraderUpdateDto);
        return ResponseEntity.ok().body(smallTraderAdminDto);
    }

    @PutMapping("/change-account-status/{id}")
    public ResponseEntity<SmallTraderAdminDto> changeEmployeeAccountStatus(@PathVariable Long id) {
        SmallTraderAdminDto smallTraderAdminDto = employeeService.changeAccountStatus(id);
        return ResponseEntity.ok().body(smallTraderAdminDto);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<SmallTraderAdminDto> deleteEmployee(@PathVariable Long id) {
        SmallTraderAdminDto smallTraderAdminDto = employeeService.deleteEmployee(id);
        return ResponseEntity.ok().body(smallTraderAdminDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SmallTraderAdminDto> getById(@PathVariable Long id) {
        SmallTraderAdminDto smallTraderAdminDto = employeeService.findById(id);
        return ResponseEntity.ok().body(smallTraderAdminDto);
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
