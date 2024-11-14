package people_service.dto.customer;

public record CustomerRegisterDto(
        String firstName,
        String lastName,
        String dateOfBirth,
        String gender,
        String address,
        String phoneNumber,
        String email,
        String password
) {
}
