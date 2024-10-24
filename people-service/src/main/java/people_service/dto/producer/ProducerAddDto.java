package people_service.dto.producer;

public record ProducerAddDto(
        String firstName,
        String lastName,
        String gender,
        String address,
        String phoneNumber,
        String email,
        Long smallTraderId
) {
}
