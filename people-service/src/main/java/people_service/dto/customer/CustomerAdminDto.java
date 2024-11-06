package people_service.dto.customer;

import people_service.model.Customer;
import java.time.format.DateTimeFormatter;

public record CustomerAdminDto(
        Long id,
        String firstName,
        String lastName,
        String address,
        String phoneNumber,
        String gender,
        String dateOfBirth,
        String email,
        Long smallTraderId
) {
    public static CustomerAdminDto fromCustomer(Customer customer) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String gender = customer.getGender().name().equals("MALE") ? "Nam" : "Nữ";
        return new CustomerAdminDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAddress(),
                customer.getPhoneNumber(),
                gender,
                customer.getDateOfBirth().format(formatter),
                customer.getEmail(),
                customer.getSmallTraderId()
        );
    }
}
