package product_service.dto.smallTrader;

public record SmallTraderAdminDto(
        Long id,
        String firstName,
        String lastName,
        String dateOfBirth,
        String gender,
        String address,
        String phoneNumber,
        String email,
        Boolean accountLocked,
        String role
) {
}
