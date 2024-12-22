package people_service.dto.employee;

import people_service.model.Customer;
import people_service.model.Employee;

import java.time.format.DateTimeFormatter;

public record EmployeeAdminDto(
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
    public static EmployeeAdminDto fromEmployee(Employee employee) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String gender = employee.getGender().name().equals("MALE") ? "Nam" : "Nữ";
        return new EmployeeAdminDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getAddress(),
                employee.getPhoneNumber(),
                gender,
                employee.getDateOfBirth().format(formatter),
                employee.getEmail(),
                employee.getSmallTrader().getId()
        );
    }
}
