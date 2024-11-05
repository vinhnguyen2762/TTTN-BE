package people_service.dto.debtDetail;

import people_service.model.DebtDetail;

import java.time.format.DateTimeFormatter;

public record DebtDetailAdminDto(
        Long id,
        String debtAmount,
        String paidAmount,
        String debtDate,
        String paidDate,
        String debtRemaining
) {
    public static DebtDetailAdminDto fromDebtDetail(DebtDetail debtDetail) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String paidDate = "---";
        if (debtDetail.getPaidDate() != null) {
            paidDate = debtDetail.getPaidDate().format(formatter);
        }
        Long debtRemaining = debtDetail.getDebtAmount() - debtDetail.getPaidAmount();
        return new DebtDetailAdminDto(
                debtDetail.getId(),
                debtDetail.getDebtAmount().toString(),
                debtDetail.getPaidAmount().toString(),
                debtDetail.getDebtDate().format(formatter),
                paidDate,
                debtRemaining.toString()
        );
    }
}
