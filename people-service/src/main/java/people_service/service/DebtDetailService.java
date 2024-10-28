package people_service.service;

import people_service.dto.debtDetail.DebtDetailAddDto;
import people_service.dto.debtDetail.DebtDetailUpdateDto;

import java.util.List;

public interface DebtDetailService {
    public List<DebtDetailAddDto> getAllDebtDetailProducer(Long id);
    public Long addDebtDetail(Long id, DebtDetailAddDto debtDetailAddDto);
    public Long updateDebtDetail(Long id, DebtDetailUpdateDto debtDetailUpdateDto);
}
