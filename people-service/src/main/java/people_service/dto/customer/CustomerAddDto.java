package people_service.dto.customer;

public record CustomerAddDto(
        String firstName,
        String lastName,
        String dateOfBirth,
        String gender,
        String address,
        String phoneNumber
) {
}
