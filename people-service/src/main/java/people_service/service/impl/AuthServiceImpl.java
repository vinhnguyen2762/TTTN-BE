package people_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.dto.smallTrader.SmallTraderLocalStorageDto;
import people_service.exception.AccountLockedException;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.AuthenticationRequest;
import people_service.model.SmallTrader;
import people_service.model.RegistrationRequest;
import people_service.repository.SmallTraderRepository;
import people_service.service.AuthService;
import people_service.service.RegistrationService;
import people_service.utils.Constants;

import static people_service.utils.PasswordHashing.checkPassword;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegistrationService registrationService;
    private final SmallTraderRepository smallTraderRepository;

    public Long register(RegistrationRequest request) {
        Long rs = registrationService.register(request);
        return rs;
    }

    public SmallTraderLocalStorageDto login(AuthenticationRequest request) {
        SmallTrader smallTrader = smallTraderRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND, request.getEmail())));
        boolean isMatch = checkPassword(request.getPassword(), smallTrader.getPassword());
        if (smallTrader.getStatus() == false) {
            throw new FailedException(String.format(Constants.ErrorMessage.USER_NOT_EXIST, request.getEmail()));
        } else if (smallTrader.getLocked() == true) {
            throw new AccountLockedException(String.format(Constants.ErrorMessage.ACCOUNT_IS_LOCKED, request.getEmail()));
        } else if (!isMatch) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
        }
        return SmallTraderLocalStorageDto.fromSmallTrader(smallTrader);
    }
}
