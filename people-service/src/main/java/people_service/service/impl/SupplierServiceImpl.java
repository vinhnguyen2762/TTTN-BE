package people_service.service.impl;

import org.springframework.stereotype.Service;
import people_service.dto.supplier.SupplierAddDto;
import people_service.dto.supplier.SupplierAdminDto;
import people_service.dto.supplier.SupplierSearchDto;
import people_service.dto.supplier.SupplierUpdateDto;
import people_service.exception.DuplicateException;
import people_service.exception.NotFoundException;
import people_service.model.SmallTrader;
import people_service.model.Supplier;
import people_service.repository.SmallTraderRepository;
import people_service.repository.SupplierRepository;
import people_service.service.SupplierService;
import people_service.utils.Constants;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SmallTraderRepository smallTraderRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository, SmallTraderRepository smallTraderRepository) {
        this.supplierRepository = supplierRepository;
        this.smallTraderRepository = smallTraderRepository;
    }

    public List<SupplierAdminDto> getAllSupplierAdmin() {
        List<Supplier> list = supplierRepository.findAll();
        return list.stream().map(s -> {
            SmallTrader smallTrader = smallTraderRepository.findById(s.getSmallTraderId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, s.getSmallTraderId()))
            );
            String smallTraderName = smallTrader.getFirstName() + " " + smallTrader.getLastName();
            return SupplierAdminDto.fromSupplier(s, smallTraderName);
        }).toList();
    }

    @Override
    public List<SupplierAdminDto> getAllSupplierSmallTrader(Long id) {
        List<Supplier> list = supplierRepository.findBySmallTraderId(id);
        return list.stream().map(s -> {
            SmallTrader smallTrader = smallTraderRepository.findById(s.getSmallTraderId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, s.getSmallTraderId()))
            );
            String smallTraderName = smallTrader.getFirstName() + " " + smallTrader.getLastName();
            return SupplierAdminDto.fromSupplier(s, smallTraderName);
        }).toList();
    }

    public Long addSupplier(SupplierAddDto supplierAddDto) {
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
                supplierAddDto.taxId(),
                supplierAddDto.smallTraderId());
        supplierRepository.saveAndFlush(supplierAdd);
        return supplierAdd.getId();
    }

    public Long updateSupplier(Long id, SupplierUpdateDto supplierUpdateDto) {
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
        return supplier.getId();
    }

    public Long deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id)));
        if (supplier.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id));
        }
        supplier.setStatus(false);
        supplierRepository.saveAndFlush(supplier);
        return supplier.getId();
    }

    public SupplierAdminDto findById(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id)));
        SmallTrader smallTrader = smallTraderRepository.findById(supplier.getSmallTraderId()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, supplier.getSmallTraderId()))
        );
        String smallTraderName = smallTrader.getFirstName() + " " + smallTrader.getLastName();
        return SupplierAdminDto.fromSupplier(supplier, smallTraderName);
    }

    public SupplierSearchDto findByTaxIdSearch(String taxId) {
        Supplier supplier = supplierRepository.findByTaxIdSearch(taxId).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND_TAX_ID, taxId)));
        String fullName = supplier.getFirstName() + " " + supplier.getLastName();
        return new SupplierSearchDto(
                supplier.getId().toString(),
                fullName
        );
    }
}
