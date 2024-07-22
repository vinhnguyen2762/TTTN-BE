package people_service.dto.supplier;

public record SupplierAddDto(
        String firstName,
        String lastName,
        String address,
        String email,
        String phoneNumber,
        String taxId
) {
}
