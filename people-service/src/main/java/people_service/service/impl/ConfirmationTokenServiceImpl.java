//package people_service.service.impl;
//
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//import people_service.exception.NotFoundException;
//import people_service.model.ConfirmationToken;
//import people_service.repository.ConfirmationTokenRepository;
//import people_service.service.ConfirmationTokenService;
//import people_service.utils.Constants;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Service
//@AllArgsConstructor
//public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
//
//    private final ConfirmationTokenRepository confirmationTokenRepository;
//
//    public void saveConfirmationToken(ConfirmationToken token) {
//        confirmationTokenRepository.save(token);
//    }
//
//    public Optional<ConfirmationToken> getToken(String token) {
//        return confirmationTokenRepository.findByToken(token);
//    }
//
//    public void setConfirmedAt(String confirmToken) {
//        ConfirmationToken tokenFound = confirmationTokenRepository.findByToken(confirmToken).orElseThrow(() -> new NotFoundException(String.format(Constants.ErrorMessage.TOKEN_NOT_FOUND, confirmToken)));
//        tokenFound.setConfirmedAt(LocalDateTime.now());
//        confirmationTokenRepository.save(tokenFound);
//    }
//}
