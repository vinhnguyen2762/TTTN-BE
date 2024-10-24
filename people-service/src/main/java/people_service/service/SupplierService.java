package people_service.service;

import people_service.dto.supplier.SupplierAddDto;
import people_service.dto.supplier.SupplierAdminDto;
import people_service.dto.supplier.SupplierSearchDto;
import people_service.dto.supplier.SupplierUpdateDto;

import java.util.List;

public interface SupplierService {
    public List<SupplierAdminDto> getAllSupplierAdmin();
    public SupplierAdminDto addSupplier(SupplierAddDto supplierAddDto);
    public SupplierAdminDto updateSupplier(Long id, SupplierUpdateDto supplierUpdateDto);
    public SupplierAdminDto deleteSupplier(Long id);
    public SupplierAdminDto findById(Long id);
    public SupplierSearchDto findByTaxIdSearch(String taxId);
}
