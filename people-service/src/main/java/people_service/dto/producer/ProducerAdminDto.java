package people_service.dto.producer;

import people_service.dto.debtDetail.DebtDetailAdminDto;
import people_service.model.Customer;
import people_service.model.Producer;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record ProducerAdminDto(
        Long id,
        String firstName,
        String lastName,
        String address,
        String phoneNumber,
        String gender,
        String email,
        String smallTraderName,
        List<DebtDetailAdminDto> list
) {
    public static ProducerAdminDto fromProducer(Producer producer, String smallTraderName) {
        String gender = producer.getGender().name().equals("MALE") ? "Nam" : "Nữ";

        List<DebtDetailAdminDto> list = producer.getDebtDetails().stream()
                .map(DebtDetailAdminDto::fromDebtDetail).toList();

        return new ProducerAdminDto(
                producer.getId(),
                producer.getFirstName(),
                producer.getLastName(),
                producer.getAddress(),
                producer.getPhoneNumber(),
                gender,
                producer.getEmail(),
                smallTraderName,
                list
        );
    }
}
