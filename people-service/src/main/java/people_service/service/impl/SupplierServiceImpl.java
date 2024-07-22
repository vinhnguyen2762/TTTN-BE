package people_service.service.impl;

import org.springframework.stereotype.Service;
import people_service.dto.supplier.SupplierAddDto;
import people_service.dto.supplier.SupplierAdminDto;
import people_service.dto.supplier.SupplierUpdateDto;
import people_service.exception.DuplicateException;
import people_service.exception.NotFoundException;
import people_service.model.Supplier;
import people_service.repository.SupplierRepository;
import people_service.service.SupplierService;
import people_service.utils.Constants;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<SupplierAdminDto> getAllSupplierAdmin() {
        List<Supplier> list = supplierRepository.findAll();
        return list.stream().map(SupplierAdminDto::fromSupplier).toList();
    }

    public SupplierAdminDto addSupplier(SupplierAddDto supplierAddDto) {
        Boolean isExist = supplierRepository.findByTaxId(supplierAddDto.taxId()).isPresent();
        if (isExist) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.SUPPLIER_ALREADY_TAKEN, supplierAddDto.taxId()));
        }

        Supplier supplierAdd = new Supplier(
                supplierAddDto.firstName(),
                supplierAddDto.lastName(),
                supplierAddDto.address(),
                supplierAddDto.email(),
                supplierAddDto.phoneNumber(),
                supplierAddDto.taxId());
        supplierRepository.saveAndFlush(supplierAdd);
        return SupplierAdminDto.fromSupplier(supplierAdd);
    }

    public SupplierAdminDto updateSupplier(Long id, SupplierUpdateDto supplierUpdateDto) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id)));
        if (supplier.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id));
        }

        supplier.setFirstName(supplierUpdateDto.firstName());
        supplier.setLastName(supplierUpdateDto.lastName());
        supplier.setAddress(supplierUpdateDto.address());
        supplier.setPhoneNumber(supplierUpdateDto.phoneNumber());
        supplier.setEmail(supplierUpdateDto.email());
        supplierRepository.saveAndFlush(supplier);
        return SupplierAdminDto.fromSupplier(supplier);
    }

    public SupplierAdminDto deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id)));
        if (supplier.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id));
        }
        supplier.setStatus(false);
        supplierRepository.saveAndFlush(supplier);
        return SupplierAdminDto.fromSupplier(supplier);
    }
}
