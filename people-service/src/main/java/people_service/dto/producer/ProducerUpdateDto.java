package people_service.dto.producer;

public record ProducerUpdateDto(
        String firstName,
        String lastName,
        String gender,
        String address,
        String phoneNumber,
        String email
) {
}
