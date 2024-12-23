package people_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.dto.smallTrader.SmallTraderRevenueDto;
import people_service.dto.smallTrader.SmallTraderUpdateDto;
import people_service.enums.Gender;
import people_service.exception.DuplicateException;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.ChangePasswordRequest;
import people_service.model.SmallTrader;
import people_service.repository.CustomerRepository;
import people_service.repository.EmployeeRepository;
import people_service.repository.SmallTraderRepository;
import people_service.service.SmallTraderService;
import people_service.utils.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static people_service.utils.PasswordHashing.checkPassword;
import static people_service.utils.PasswordHashing.hashPassword;

@Service
@RequiredArgsConstructor
public class SmallTraderServiceImpl implements SmallTraderService {

    private final SmallTraderRepository smallTraderRepository;
    private final EmployeeRepository employeeRepository;
//    private final ConfirmationTokenService confirmationTokenService;
//    private final ConfirmationTokenRepository confirmationTokenRepository;

//    public String signUpUser(SmallTrader smallTrader) {
//        boolean isPhoneNumberExists = smallTraderRepository.findByPhoneNumber(smallTrader.getPhoneNumber()).isPresent();
//        if (isPhoneNumberExists) {
//            throw new NotFoundException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, smallTrader.getPhoneNumber()));
//        }
//
//        boolean isEmailExists = smallTraderRepository.findByEmail(smallTrader.getEmail()).isPresent();
//        if (isEmailExists) {
//            SmallTrader smallTraderFound = smallTraderRepository.findByEmail(smallTrader.getEmail()).orElseThrow();
//            ConfirmationToken tokenFound = confirmationTokenRepository.findBySmallTraderId(smallTraderFound.getId()).orElseThrow();
//            if (tokenFound.getConfirmedAt() != null) {
//                throw new DuplicateException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, smallTrader.getEmail()));
//            }
//
//            else {
//                confirmationTokenRepository.delete(tokenFound);
//                smallTraderRepository.delete(smallTraderFound);
//            }
//        }
//
//        String hashedPassword = hashPassword(smallTrader.getPassword());
//        smallTrader.setPassword(hashedPassword);
//
//        smallTraderRepository.saveAndFlush(smallTrader);
//
//        //create a random token
//        String confirmToken = UUID.randomUUID().toString();
//        ConfirmationToken confirmationToken = new ConfirmationToken(
//                confirmToken,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusMinutes(15),
//                smallTrader);
//
//        //save it to database
//        confirmationTokenService.saveConfirmationToken(confirmationToken);
//
//        return confirmToken;
//    }

    public void enableAppUser(String email) {
        SmallTrader smallTrader = smallTraderRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND, email)));
        smallTrader.setStatus(true);
        smallTraderRepository.save(smallTrader);
    }


    public List<SmallTraderAdminDto> getAllSmallTraderAdmin() {
        List<SmallTrader> list = smallTraderRepository.findAll();
        return list.stream().map(SmallTraderAdminDto::fromSmallTrader).toList();
    }

    public List<SmallTraderRevenueDto> getAllSmallTraderRevenue() {
        List<SmallTrader> list = smallTraderRepository.findAll();
        return list.stream().map(s -> {
            String fullName = s.getFirstName() + " " + s.getLastName();
            return new SmallTraderRevenueDto(s.getId(), fullName, s.getPhoneNumber());
        }).toList();
    }

    public SmallTraderAdminDto updateSmallTrader(Long id, SmallTraderUpdateDto smallTraderUpdateDto) {
        SmallTrader smallTrader = smallTraderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id)));
        if (smallTrader.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id));
        }

        String oldEmail = smallTrader.getEmail();
        String oldPhoneNumber = smallTrader.getPhoneNumber();

        if (!smallTraderUpdateDto.phoneNumber().equals(oldPhoneNumber)) {
            Boolean isPhoneNumberExistSmallTrader = smallTraderRepository.findByPhoneNumber(smallTraderUpdateDto.phoneNumber()).isPresent();
            Boolean isPhoneNumberExistEmployee = employeeRepository.findByPhoneNumber(smallTraderUpdateDto.phoneNumber()).isPresent();
            if (!isPhoneNumberExistSmallTrader && !isPhoneNumberExistEmployee) {
                smallTrader.setPhoneNumber(smallTraderUpdateDto.phoneNumber());
            } else {
                throw new FailedException(String.format(Constants.ErrorMessage.PHONE_NUMBER_ALREADY_TAKEN, smallTraderUpdateDto.phoneNumber()));
            }
        }

        if (!smallTraderUpdateDto.email().equals(oldEmail)) {
            boolean isEmailExistSmallTrader = smallTraderRepository.findByEmail(smallTraderUpdateDto.email()).isPresent();
            boolean isEmailExistsCustomer = employeeRepository.findByEmail(smallTraderUpdateDto.email()).isPresent();
            if (!isEmailExistSmallTrader && !isEmailExistsCustomer) {
                smallTrader.setEmail(smallTraderUpdateDto.email());
            } else {
                throw new DuplicateException(String.format(Constants.ErrorMessage.EMAIL_ALREADY_TAKEN, smallTraderUpdateDto.email()));
            }
        }

        LocalDate date = LocalDate.parse(smallTraderUpdateDto.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
        Gender gender = smallTraderUpdateDto.gender().equals("Nam") ? Gender.MALE : Gender.FEMALE;

        smallTrader.setFirstName(smallTraderUpdateDto.firstName());
        smallTrader.setLastName(smallTraderUpdateDto.lastName());
        smallTrader.setDateOfBirth(date);
        smallTrader.setGender(gender);
        smallTrader.setAddress(smallTraderUpdateDto.address());
        smallTraderRepository.saveAndFlush(smallTrader);
        return SmallTraderAdminDto.fromSmallTrader(smallTrader);
    }

    public SmallTraderAdminDto deleteSmallTrader(Long id) {
        SmallTrader smallTrader = smallTraderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id)));
        if (smallTrader.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id));
        }
        smallTrader.setStatus(false);
        smallTraderRepository.saveAndFlush(smallTrader);
        return SmallTraderAdminDto.fromSmallTrader(smallTrader);
    }

    public SmallTraderAdminDto changeAccountStatus(Long id) {
        SmallTrader smallTrader = smallTraderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id)));
        smallTrader.setLocked(!smallTrader.getLocked());
        smallTraderRepository.saveAndFlush(smallTrader);
        return SmallTraderAdminDto.fromSmallTrader(smallTrader);
    }

    public Long changePassword(Long id, ChangePasswordRequest changePasswordRequest) {
        SmallTrader smallTrader = smallTraderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND_ID, id)));
        boolean isMatch = checkPassword(changePasswordRequest.getOldPassword(), smallTrader.getPassword());
        if (!isMatch) {
            throw new FailedException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
        } else {
            smallTrader.setPassword(hashPassword(changePasswordRequest.getNewPassword()));
            smallTraderRepository.saveAndFlush(smallTrader);
        }
        return smallTrader.getId();
    }

    public SmallTraderAdminDto findById(Long id) {
        SmallTrader smallTrader = smallTraderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, id)));
        return SmallTraderAdminDto.fromSmallTrader(smallTrader);
    }

    public Long countSmallTraderByStatusTrue() {
        Long rs = smallTraderRepository.countEmployeeByStatusTrue();
        return rs;
    }

}
