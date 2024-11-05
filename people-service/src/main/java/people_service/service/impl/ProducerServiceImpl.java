package people_service.service.impl;


import org.springframework.stereotype.Service;
import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerSearchDto;
import people_service.dto.customer.CustomerUpdateDto;
import people_service.dto.debtDetail.DebtDetailAdminDto;
import people_service.dto.producer.ProducerAddDto;
import people_service.dto.producer.ProducerAdminDto;
import people_service.dto.producer.ProducerUpdateDto;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.enums.Gender;
import people_service.exception.DuplicateException;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.Customer;
import people_service.model.DebtDetail;
import people_service.model.Producer;
import people_service.model.SmallTrader;
import people_service.repository.ProducerRepository;
import people_service.repository.SmallTraderRepository;
import people_service.service.ProducerService;
import people_service.utils.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProducerServiceImpl implements ProducerService {

    private final ProducerRepository producerRepository;
    private final SmallTraderRepository smallTraderRepository;

    public ProducerServiceImpl(ProducerRepository producerRepository, SmallTraderRepository smallTraderRepository) {
        this.producerRepository = producerRepository;
        this.smallTraderRepository = smallTraderRepository;
    }

    public List<ProducerAdminDto> getAllProducerAdmin() {
        List<Producer> list = producerRepository.findAll();
        return list.stream().map(p -> {
            List<DebtDetail> debtDetailList = p.getDebtDetails();
            Long remainingDebt = 0L;
            for (DebtDetail item : debtDetailList) {
                Long rs = item.getDebtAmount() - item.getPaidAmount();
                remainingDebt += rs;
            }
            List<DebtDetailAdminDto> debtDetailAdminDtoList = p.getDebtDetails().stream()
                    .map(DebtDetailAdminDto::fromDebtDetail).toList();
            return ProducerAdminDto.fromProducer(p, remainingDebt.toString(), debtDetailAdminDtoList);
        }).toList();
    }

    public List<ProducerAdminDto> getAllProducerSmallTrader(Long id) {
        List<Producer> list = producerRepository.findBySmallTrader(id);
        return list.stream().map(p -> {
            List<DebtDetail> debtDetailList = p.getDebtDetails();
            Long remainingDebt = 0L;
            for (DebtDetail item : debtDetailList) {
                Long rs = item.getDebtAmount() - item.getPaidAmount();
                remainingDebt += rs;
            }
            List<DebtDetailAdminDto> debtDetailAdminDtoList = p.getDebtDetails().stream()
                    .map(DebtDetailAdminDto::fromDebtDetail).toList();
            return ProducerAdminDto.fromProducer(p, remainingDebt.toString(), debtDetailAdminDtoList);
        }).toList();
    }

    public Long addProducer(ProducerAddDto producerAddDto) {
        Boolean isPhoneNumberExist = producerRepository.findByPhoneNumber(producerAddDto.phoneNumber()).isPresent();
        if (isPhoneNumberExist) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, producerAddDto.phoneNumber()));
        }

        Boolean isEmailExist = producerRepository.findByEmail(producerAddDto.email()).isPresent();
        if (isEmailExist) {
            throw new FailedException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, producerAddDto.email()));
        }

        Gender gender = producerAddDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;

        Producer producerAdd = new Producer(
                producerAddDto.firstName(),
                producerAddDto.lastName(),
                gender,
                producerAddDto.address(),
                producerAddDto.phoneNumber(),
                producerAddDto.email(),
                producerAddDto.smallTraderId()
        );
        producerRepository.saveAndFlush(producerAdd);
        return producerAdd.getId();
    }

    public Long updateProducer(Long id, ProducerUpdateDto producerUpdateDto) {
        Producer producer = producerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id)));
        if (producer.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id));
        }

        Gender gender = producerUpdateDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;

        String oldPhoneNumber = producer.getPhoneNumber();
        String oldEmail = producer.getEmail();

        // if phone number is new, check if the new phone number exist
        if (!producerUpdateDto.phoneNumber().equals(oldPhoneNumber)) {
            Boolean isPhoneNumberExist = producerRepository.findByPhoneNumber(producerUpdateDto.phoneNumber()).isPresent();
            if (!isPhoneNumberExist) {
                producer.setPhoneNumber(producerUpdateDto.phoneNumber());
            } else {
                throw new FailedException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, producerUpdateDto.phoneNumber()));
            }
        }

        // if email is new, check if the new email exist
        if (!producerUpdateDto.email().equals(oldEmail)) {
            Boolean isEmailExist = producerRepository.findByEmail(producerUpdateDto.email()).isPresent();
            if (!isEmailExist) {
                producer.setEmail(producerUpdateDto.email());
            } else {
                throw new DuplicateException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, producerUpdateDto.email()));
            }
        }

        producer.setFirstName(producerUpdateDto.firstName());
        producer.setLastName(producerUpdateDto.lastName());
        producer.setGender(gender);
        producer.setAddress(producerUpdateDto.address());
        producerRepository.saveAndFlush(producer);
        return producer.getId();
    }

    public Long deleteProducer(Long id) {
        Producer producer = producerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id)));
        if (producer.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id));
        }

        List<DebtDetail> debtDetailList = producer.getDebtDetails();
        Long remainingDebt = 0L;
        for (DebtDetail item : debtDetailList) {
            Long rs = item.getDebtAmount() - item.getPaidAmount();
            remainingDebt += rs;
        }

        if (remainingDebt.equals(0L)) {
            producer.setStatus(false);
            producerRepository.saveAndFlush(producer);
        }
        else {
            throw new FailedException(String.format(Constants.ErrorMessage.PRODUCER_CANT_DELETE, id));
        }
        return producer.getId();
    }

    public Long countProducerByStatusTrue() {
        Long rs = producerRepository.countProducerByStatusTrue();
        return rs;
    }
}
