package people_service.dto.employee;

public record EmployeeUpdateDto(
        String firstName,
        String lastName,
        String dateOfBirth,
        String gender,
        String address,
        String phoneNumber
) {
}
