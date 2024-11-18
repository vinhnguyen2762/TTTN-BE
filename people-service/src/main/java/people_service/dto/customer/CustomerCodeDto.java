package people_service.dto.customer;

public record CustomerCodeDto(
        String email,
        String codeReceive,
        String code
) {
}
