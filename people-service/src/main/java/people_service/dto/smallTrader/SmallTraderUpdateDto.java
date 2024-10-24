package people_service.dto.smallTrader;

public record SmallTraderUpdateDto(
        String firstName,
        String lastName,
        String dateOfBirth,
        String gender,
        String address,
        String phoneNumber,
        String email
) {
}
