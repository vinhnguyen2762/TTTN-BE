package people_service.service.impl;

import org.springframework.stereotype.Service;
import people_service.dto.supplier.SupplierAddDto;
import people_service.dto.supplier.SupplierAdminDto;
import people_service.dto.supplier.SupplierSearchDto;
import people_service.exception.AccountLockedException;
import people_service.exception.DuplicateException;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.SmallTrader;
import people_service.model.Supplier;
import people_service.repository.SmallTraderRepository;
import people_service.repository.SupplierRepository;
import people_service.service.SupplierService;
import people_service.service.client.ProductFeignClient;
import people_service.utils.Constants;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SmallTraderRepository smallTraderRepository;
    private final ProductFeignClient productFeignClient;

    public SupplierServiceImpl(SupplierRepository supplierRepository, SmallTraderRepository smallTraderRepository, ProductFeignClient productFeignClient) {
        this.supplierRepository = supplierRepository;
        this.smallTraderRepository = smallTraderRepository;
        this.productFeignClient = productFeignClient;
    }

    public List<SupplierAdminDto> getAllSupplierAdmin() {
        List<Supplier> list = supplierRepository.findAll();
        return list.stream().map(SupplierAdminDto::fromSupplier).toList();
    }

    @Override
    public List<SupplierAdminDto> getAllSupplierSmallTrader(Long id) {
        List<Supplier> list = supplierRepository.findBySmallTraderId(id);
        return list.stream().map(SupplierAdminDto::fromSupplier).toList();
    }

    public Long addSupplier(SupplierAddDto supplierAddDto) {
        Boolean isTaxIdExist = supplierRepository.findByTaxId(supplierAddDto.taxId()).isPresent();
        if (isTaxIdExist) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.SUPPLIER_ALREADY_TAKEN, supplierAddDto.taxId()));
        }

        Boolean isPhoneNumberExist = supplierRepository.findByPhoneNumber(supplierAddDto.phoneNumber()).isPresent();
        if (isPhoneNumberExist) {
            throw new FailedException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, supplierAddDto.phoneNumber()));
        }

        Boolean isEmailExist = supplierRepository.findByEmail(supplierAddDto.email()).isPresent();
        if (isEmailExist) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, supplierAddDto.email()));
        }

        SmallTrader smallTrader = smallTraderRepository.findById(supplierAddDto.smallTraderId()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, supplierAddDto.smallTraderId())));

        Supplier supplierAdd = new Supplier(
                supplierAddDto.firstName(),
                supplierAddDto.lastName(),
                supplierAddDto.address(),
                supplierAddDto.email(),
                supplierAddDto.phoneNumber(),
                supplierAddDto.taxId(),
                smallTrader);
        supplierRepository.saveAndFlush(supplierAdd);
        return supplierAdd.getId();
    }

    public Long updateSupplier(Long id, SupplierAddDto supplierAddDto) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id)));
        if (supplier.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id));
        }

        String oldTaxId = supplier.getTaxId();
        String oldPhoneNumber = supplier.getPhoneNumber();
        String oldEmail = supplier.getEmail();

        if (!supplierAddDto.taxId().equals(oldTaxId)) {
            Boolean isTaxIdExist = supplierRepository.findByTaxId(supplierAddDto.taxId()).isPresent();
            if (!isTaxIdExist) {
                supplier.setTaxId(supplierAddDto.taxId());
            } else {
                throw new DuplicateException(String.format(Constants.ErrorMessage.SUPPLIER_ALREADY_TAKEN, supplierAddDto.taxId()));
            }
        }

        // if phone number is new, check if the new phone number exist
        if (!supplierAddDto.phoneNumber().equals(oldPhoneNumber)) {
            Boolean isPhoneNumberExist = supplierRepository.findByPhoneNumber(supplierAddDto.phoneNumber()).isPresent();
            if (!isPhoneNumberExist) {
                supplier.setPhoneNumber(supplierAddDto.phoneNumber());
            } else {
                throw new FailedException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, supplierAddDto.phoneNumber()));
            }
        }

        // if email is new, check if the new email exist
        if (!supplierAddDto.email().equals(oldEmail)) {
            Boolean isEmailExist = supplierRepository.findByEmail(supplierAddDto.email()).isPresent();
            if (!isEmailExist) {
                supplier.setEmail(supplierAddDto.email());
            } else {
                throw new AccountLockedException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, supplierAddDto.email()));
            }
        }

        supplier.setFirstName(supplierAddDto.firstName());
        supplier.setLastName(supplierAddDto.lastName());
        supplier.setAddress(supplierAddDto.address());
        supplierRepository.saveAndFlush(supplier);
        return supplier.getId();
    }

    public Long deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id)));
        if (supplier.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id));
        }
        if (checkSupplierHasPurchaseOrder(id)) {
            supplier.setStatus(false);
            supplierRepository.saveAndFlush(supplier);
        }
        else {
            supplierRepository.delete(supplier);
        }
        return supplier.getId();
    }

    public SupplierAdminDto findById(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SUPPLIER_NOT_FOUND, id)));
        return SupplierAdminDto.fromSupplier(supplier);
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

    public Long countSupplierSmallTrader(Long id) {
        Long rs = supplierRepository.countSupplierSmallTrader(id);
        return rs;
    }

    private Boolean checkSupplierHasPurchaseOrder(Long id) {
        Boolean rs = productFeignClient.checkSupplierHasPurchaseOrder(id).getBody();
        return rs;
    }
}
