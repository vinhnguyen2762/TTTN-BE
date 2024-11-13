package people_service.dto.smallTrader;

import people_service.model.SmallTrader;

import java.time.format.DateTimeFormatter;

public record SmallTraderAdminDto(
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
    public static SmallTraderAdminDto fromSmallTrader(SmallTrader smallTrader) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String gender = smallTrader.getGender().name().equals("MALE") ? "Nam" : "Nữ";
        return new SmallTraderAdminDto(
                smallTrader.getId(),
                smallTrader.getFirstName(),
                smallTrader.getLastName(),
                smallTrader.getDateOfBirth().format(formatter),
                gender,
                smallTrader.getAddress(),
                smallTrader.getPhoneNumber(),
                smallTrader.getEmail(),
                smallTrader.getLocked(),
                smallTrader.getRole().name()
        );
    }
}
