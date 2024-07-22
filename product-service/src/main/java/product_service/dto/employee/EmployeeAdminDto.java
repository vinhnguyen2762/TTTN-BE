package product_service.dto.employee;

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
}
