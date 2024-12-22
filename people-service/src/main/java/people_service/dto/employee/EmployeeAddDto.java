package people_service.dto.employee;

public record EmployeeAddDto(
        String firstName,
        String lastName,
        String dateOfBirth,
        String gender,
        String address,
        String phoneNumber,
        String email,
        Long smallTraderId
) {
}
