package people_service.dto.producer;

import people_service.model.Customer;
import people_service.model.Producer;

import java.time.format.DateTimeFormatter;

public record ProducerAdminDto(
        Long id,
        String firstName,
        String lastName,
        String address,
        String phoneNumber,
        String gender,
        String email,
        String smallTraderName
) {
    public static ProducerAdminDto fromProducer(Producer producer, String smallTraderName) {
        String gender = producer.getGender().name().equals("MALE") ? "Nam" : "Nữ";
        return new ProducerAdminDto(
                producer.getId(),
                producer.getFirstName(),
                producer.getLastName(),
                producer.getAddress(),
                producer.getPhoneNumber(),
                gender,
                producer.getEmail(),
                smallTraderName
        );
    }
}
