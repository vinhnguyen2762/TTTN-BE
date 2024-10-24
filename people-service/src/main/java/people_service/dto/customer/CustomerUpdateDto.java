package people_service.dto.customer;

public record CustomerUpdateDto(
        String firstName,
        String lastName,
        String dateOfBirth,
        String gender,
        String address,
        String phoneNumber,
        String email
) {
}
