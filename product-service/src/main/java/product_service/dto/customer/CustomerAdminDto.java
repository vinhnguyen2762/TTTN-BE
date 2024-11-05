package product_service.dto.customer;

public record CustomerAdminDto(
        Long id,
        String firstName,
        String lastName,
        String address,
        String phoneNumber,
        String gender,
        String dateOfBirth,
        String email
) {
}
