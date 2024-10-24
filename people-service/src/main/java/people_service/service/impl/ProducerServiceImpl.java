package people_service.service.impl;


import org.springframework.stereotype.Service;
import people_service.dto.customer.CustomerAddDto;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.customer.CustomerSearchDto;
import people_service.dto.customer.CustomerUpdateDto;
import people_service.dto.producer.ProducerAddDto;
import people_service.dto.producer.ProducerAdminDto;
import people_service.dto.producer.ProducerUpdateDto;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.enums.Gender;
import people_service.exception.DuplicateException;
import people_service.exception.NotFoundException;
import people_service.model.Customer;
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
            SmallTrader smallTrader = smallTraderRepository.findById(p.getSmallTraderId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, p.getSmallTraderId()))
            );
            String smallTraderName = smallTrader.getFirstName() + " " + smallTrader.getLastName();
            return ProducerAdminDto.fromProducer(p, smallTraderName);
        }).toList();
    }

    public List<ProducerAdminDto> getAllProducerSmallTrader(Long id) {
        List<Producer> list = producerRepository.findBySmallTraderId(id);
        return list.stream().map(p -> {
            SmallTrader smallTrader = smallTraderRepository.findById(p.getSmallTraderId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, p.getSmallTraderId()))
            );
            String smallTraderName = smallTrader.getFirstName() + " " + smallTrader.getLastName();
            return ProducerAdminDto.fromProducer(p, smallTraderName);
        }).toList();
    }

    public Long addProducer(ProducerAddDto producerAddDto) {
        Boolean isExist = producerRepository.findByEmail(producerAddDto.email()).isPresent();
        if (isExist) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, producerAddDto.email()));
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

        String oldEmail = producer.getEmail();

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
        producer.setPhoneNumber((producerUpdateDto.phoneNumber()));
        producer.setEmail((producerUpdateDto.email()));
        producerRepository.saveAndFlush(producer);
        return producer.getId();
    }

    public Long deleteProducer(Long id) {
        Producer producer = producerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id)));
        if (producer.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id));
        }
        producer.setStatus(false);
        producerRepository.saveAndFlush(producer);
        return producer.getId();
    }

    public Long countProducerByStatusTrue() {
        Long rs = producerRepository.countProducerByStatusTrue();
        return rs;
    }
}
