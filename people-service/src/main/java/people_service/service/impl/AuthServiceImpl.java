package people_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.smallTrader.SmallTraderCodeDto;
import people_service.dto.smallTrader.SmallTraderForgetPasswordDto;
import people_service.dto.smallTrader.SmallTraderLocalStorageDto;
import people_service.exception.AccountLockedException;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.AuthenticationRequest;
import people_service.model.Customer;
import people_service.model.SmallTrader;
import people_service.model.RegistrationRequest;
import people_service.repository.CustomerRepository;
import people_service.repository.SmallTraderRepository;
import people_service.service.AuthService;
import people_service.service.EmailService;
import people_service.service.RegistrationService;
import people_service.utils.Constants;

import java.security.SecureRandom;

import static people_service.utils.PasswordHashing.checkPassword;
import static people_service.utils.PasswordHashing.hashPassword;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegistrationService registrationService;
    private final SmallTraderRepository smallTraderRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private static final int OTP_LENGTH = 6;
    private final SecureRandom secureRandom = new SecureRandom();

    public Long register(RegistrationRequest request) {
        Long rs = registrationService.register(request);
        return rs;
    }

    public SmallTraderLocalStorageDto login(AuthenticationRequest request) {
        SmallTrader smallTrader = smallTraderRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND, request.getEmail()))
        );
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

    public Long checkCodeEmail(SmallTraderCodeDto smallTraderCodeDto) {
        if (smallTraderCodeDto.code().equals(smallTraderCodeDto.codeReceive())) {
            SmallTrader smallTrader = smallTraderRepository.findByEmail(smallTraderCodeDto.email()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND, smallTraderCodeDto.email())));
            return smallTrader.getId();
        } else {
            throw new FailedException(String.format(Constants.ErrorMessage.VERIFY_CODE_FALSE, smallTraderCodeDto.email()));
        }
    }

    public String sendCodeToEmail(String email) {
        String code = generateCode();
        emailService.sendMessageWithAttachment(email, buildEmailCode("Verify your email", code));
        return code;
    }

    public Long changeForgetPassword(SmallTraderForgetPasswordDto smallTraderForgetPasswordDto) {
        SmallTrader smallTrader = smallTraderRepository.findById(smallTraderForgetPasswordDto.id()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND_ID, smallTraderForgetPasswordDto.id())));
        smallTrader.setPassword(hashPassword(smallTraderForgetPasswordDto.newPassword()));
        smallTraderRepository.saveAndFlush(smallTrader);
        return smallTrader.getId();
    }

    private String buildEmailCode(String header, String code) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">" + header + "</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">This is your code to verify your email:</p>\n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c;font-weight:bold\">" + code + "</p>\n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    private String generateCode() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            int digit = secureRandom.nextInt(10); // Số ngẫu nhiên từ 0-9
            otp.append(digit);
        }
        return otp.toString();
    }
}
