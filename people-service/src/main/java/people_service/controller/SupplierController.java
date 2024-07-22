package people_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerUpdateDto;
import people_service.dto.supplier.SupplierAddDto;
import people_service.dto.supplier.SupplierAdminDto;
import people_service.dto.supplier.SupplierUpdateDto;
import people_service.model.Supplier;
import people_service.service.SupplierService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/supplier")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<SupplierAdminDto>> getAllSupplierAdmin() {
        List<SupplierAdminDto> list = supplierService.getAllSupplierAdmin();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/add")
    public ResponseEntity<SupplierAdminDto> addSupplier(@RequestBody SupplierAddDto supplierAddDto) {
        SupplierAdminDto supplierAdminDto = supplierService.addSupplier(supplierAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierAdminDto);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<SupplierAdminDto> updateSupplier(@PathVariable Long id, @RequestBody SupplierUpdateDto supplierUpdateDto) {
        SupplierAdminDto supplierAdminDto = supplierService.updateSupplier(id, supplierUpdateDto);
        return ResponseEntity.ok().body(supplierAdminDto);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<SupplierAdminDto> deleteSupplier(@PathVariable Long id) {
        SupplierAdminDto supplierAdminDto = supplierService.deleteSupplier(id);
        return ResponseEntity.ok().body(supplierAdminDto);
    }
}
