package product_service.dto.supplier;

public record SupplierAdminDto(
        Long id,
        String firstName,
        String lastName,
        String address,
        String email,
        String phoneNumber,
        String taxId
) {
}
