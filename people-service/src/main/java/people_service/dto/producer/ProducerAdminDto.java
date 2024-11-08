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
        String remainingDebt,
        List<DebtDetailAdminDto> list,
        Long smallTraderId
) {
    public static ProducerAdminDto fromProducer(Producer producer, String remainingDebt, List<DebtDetailAdminDto> list) {
        String gender = producer.getGender().name().equals("MALE") ? "Nam" : "Nữ";

        return new ProducerAdminDto(
                producer.getId(),
                producer.getFirstName(),
                producer.getLastName(),
                producer.getAddress(),
                producer.getPhoneNumber(),
                gender,
                producer.getEmail(),
                remainingDebt,
                list,
                producer.getSmallTraderId()
        );
    }
}
