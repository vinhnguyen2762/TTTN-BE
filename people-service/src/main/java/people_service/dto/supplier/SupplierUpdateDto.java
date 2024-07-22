package people_service.dto.supplier;

public record SupplierUpdateDto(
        String firstName,
        String lastName,
        String address,
        String email,
        String phoneNumber
) {
}
