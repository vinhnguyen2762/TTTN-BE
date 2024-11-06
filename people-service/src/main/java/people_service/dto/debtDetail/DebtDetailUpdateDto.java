package people_service.dto.debtDetail;

public record DebtDetailUpdateDto(
        String debtAmount,
        String paidAmount
) {
}
