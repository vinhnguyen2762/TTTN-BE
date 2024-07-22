package people_service.dto.employee;

import people_service.model.Employee;

import java.time.format.DateTimeFormatter;

public record EmployeeAdminDto(
        Long id,
        String firstName,
        String lastName,
        String dateOfBirth,
        String gender,
        String address,
        String phoneNumber,
        String email,
        Boolean accountLocked,
        String role
) {
    public static EmployeeAdminDto fromEmployee(Employee employee) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String gender = employee.getGender().name().equals("MALE") ? "Nam" : "Nữ";
        return new EmployeeAdminDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDateOfBirth().format(formatter),
                gender,
                employee.getAddress(),
                employee.getPhoneNumber(),
                employee.getEmail(),
                employee.getLocked(),
                employee.getRole().name()
        );
    }
}
