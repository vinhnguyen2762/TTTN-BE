package people_service.service.impl;

import org.springframework.stereotype.Service;
import people_service.dto.debtDetail.DebtDetailAddDto;
import people_service.dto.debtDetail.DebtDetailUpdateDto;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.DebtDetail;
import people_service.model.Producer;
import people_service.repository.DebtDetailRepository;
import people_service.repository.ProducerRepository;
import people_service.service.DebtDetailService;
import people_service.utils.Constants;

import java.time.LocalDate;
import java.util.List;

@Service
public class DebtDetailServiceImpl implements DebtDetailService {

    private final DebtDetailRepository debtDetailRepository;
    private final ProducerRepository producerRepository;

    public DebtDetailServiceImpl(DebtDetailRepository debtDetailRepository, ProducerRepository producerRepository) {
        this.debtDetailRepository = debtDetailRepository;
        this.producerRepository = producerRepository;
    }

    public List<DebtDetailAddDto> getAllDebtDetailProducer(Long id) {
        List<DebtDetail> debtDetailList = debtDetailRepository.findAll();
        return null;
    }

    public Long addDebtDetail(Long id, DebtDetailAddDto debtDetailAddDto) {
        Producer producer = producerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCER_NOT_FOUND, id)));

        LocalDate debtDate = LocalDate.now();
        LocalDate paidDate = LocalDate.now();

        Long debtAmount = Long.parseLong(debtDetailAddDto.debtAmount());
        Long paidAmount = Long.parseLong(debtDetailAddDto.paidAmount());

        if (paidAmount > debtAmount) {
            throw new FailedException(Constants.ErrorMessage.PAID_MORE_THAN_DEBT);
        }

        if (paidAmount.equals(0L)) {
            paidDate = null;
        }

        DebtDetail debtDetailAdd = new DebtDetail(
                debtAmount,
                debtDate,
                paidAmount,
                paidDate,
                producer
        );
        debtDetailRepository.saveAndFlush(debtDetailAdd);
        return debtDetailAdd.getId();
    }

    public Long updateDebtDetail(Long id, DebtDetailUpdateDto debtDetailUpdateDto) {
        DebtDetail debtDetail = debtDetailRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.DEBT_DETAIL_NOT_FOUND, id)));

        LocalDate paidDate = LocalDate.now();
        Long paidAmount = Long.parseLong(debtDetailUpdateDto.paidAmount());

        debtDetail.setPaidAmount(paidAmount);
        debtDetail.setPaidDate(paidDate);
        debtDetailRepository.saveAndFlush(debtDetail);
        return debtDetail.getId();
    }
}
