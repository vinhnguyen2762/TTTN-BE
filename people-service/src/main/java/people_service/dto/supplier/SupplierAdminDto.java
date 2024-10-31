package people_service.dto.supplier;

import people_service.model.Customer;
import people_service.model.Supplier;

import java.time.format.DateTimeFormatter;

public record SupplierAdminDto(
        Long id,
        String firstName,
        String lastName,
        String address,
        String email,
        String phoneNumber,
        String taxId,
        String smallTraderName
) {
    public static SupplierAdminDto fromSupplier(Supplier supplier, String smallTraderName) {
        return new SupplierAdminDto(
                supplier.getId(),
                supplier.getFirstName(),
                supplier.getLastName(),
                supplier.getAddress(),
                supplier.getEmail(),
                supplier.getPhoneNumber(),
                supplier.getTaxId(),
                smallTraderName
        );
    }
}
