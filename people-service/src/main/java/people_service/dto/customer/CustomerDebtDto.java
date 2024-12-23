package people_service.dto.customer;

import people_service.model.Customer;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record CustomerDebtDto(
        Long id,
        String firstName,
        String lastName,
        String address,
        String phoneNumber,
        String gender,
        String dateOfBirth,
        String email,
        Long smallTraderId,
        String remainingDebt,
        List<CustomerOrderDebtDto> list
) {
    public static CustomerDebtDto fromCustomer(Customer customer, List<CustomerOrderDebtDto> list) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String gender = customer.getGender().name().equals("MALE") ? "Nam" : "Nữ";

        Long remainingDebt = list.stream().mapToLong(item -> Long.parseLong(item.total())).sum();

        return new CustomerDebtDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAddress(),
                customer.getPhoneNumber(),
                gender,
                customer.getDateOfBirth().format(formatter),
                customer.getEmail(),
                customer.getSmallTrader().getId(),
                remainingDebt.toString(),
                list
        );
    }
}
