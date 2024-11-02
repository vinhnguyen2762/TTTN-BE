package people_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.supplier.SupplierAddDto;
import people_service.dto.supplier.SupplierAdminDto;
import people_service.dto.supplier.SupplierSearchDto;
import people_service.dto.supplier.SupplierUpdateDto;
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

    @GetMapping("/get-all/{id}")
    public ResponseEntity<List<SupplierAdminDto>> getAllSupplierSmallTrader(@PathVariable Long id) {
        List<SupplierAdminDto> list = supplierService.getAllSupplierSmallTrader(id);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/add")
    public ResponseEntity<Long> addSupplier(@RequestBody SupplierAddDto supplierAddDto) {
        Long rs = supplierService.addSupplier(supplierAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<Long> updateSupplier(@PathVariable Long id, @RequestBody SupplierUpdateDto supplierUpdateDto) {
        Long rs = supplierService.updateSupplier(id, supplierUpdateDto);
        return ResponseEntity.ok().body(rs);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<Long> deleteSupplier(@PathVariable Long id) {
        Long rs = supplierService.deleteSupplier(id);
        return ResponseEntity.ok().body(rs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierAdminDto> getById(@PathVariable Long id) {
        SupplierAdminDto supplierAdminDto = supplierService.findById(id);
        return ResponseEntity.ok().body(supplierAdminDto);
    }

    @GetMapping("/search")
    public ResponseEntity<SupplierSearchDto> searchByPhoneTaxId(@RequestParam("taxId") String taxId) {
        SupplierSearchDto supplierSearchDto = supplierService.findByTaxIdSearch(taxId);
        return ResponseEntity.ok().body(supplierSearchDto);
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<Long> countSupplierBySmallTraderId(@PathVariable Long id) {
        Long rs = supplierService.countSupplierSmallTrader(id);
        return ResponseEntity.ok().body(rs);
    }
}
