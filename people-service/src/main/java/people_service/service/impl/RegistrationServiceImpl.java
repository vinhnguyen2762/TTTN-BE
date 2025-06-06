package people_service.service.impl;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import people_service.enums.Gender;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.*;
import people_service.repository.SmallTraderRepository;
import people_service.service.*;
import people_service.utils.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final SmallTraderService smallTraderService;
//    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final SmallTraderRepository smallTraderRepository;

    public Long register(RegistrationRequest request) {
//        boolean isValidEmail = EmailValidator.getInstance().isValid(request.getEmail());
//        if (!isValidEmail) {
//            throw new FailedException(String.format(Constants.ErrorMessage.EMAIL_NOT_VALID, request.getEmail()));
//        }
//
//        LocalDate date = LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
//        Gender gender = request.getGender().equals("Nam") ? Gender.MALE : Gender.FEMALE;
//
//        SmallTrader smallTrader = new SmallTrader(
//                request.getFirstName(),
//                request.getLastName(),
//                date,
//                gender,
//                request.getAddress(),
//                request.getPhoneNumber(),
//                request.getEmail(),
//                request.getPassword());
//        String confirmToken = smallTraderService.signUpUser(smallTrader);
//        String link = "http://localhost:9001/api/v1/auth/confirm?token=" + confirmToken;
//        emailService.sendMessageWithAttachment(request.getEmail(), buildEmail(request.getLastName(), link));
//        return smallTrader.getId();
        return null;
    }

    @Transactional
    public String confirmToken(String confirmToken) {
//        ConfirmationToken confirmationToken = confirmationTokenService
//                .getToken(confirmToken)
//                .orElseThrow(() ->
//                        new NotFoundException(String.format(Constants.ErrorMessage.TOKEN_NOT_FOUND, confirmToken)));
//
//        if (confirmationToken.getConfirmedAt() != null) {
//            return "Your email is already confirmed";
//        }
//
//        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
//
//        if (expiredAt.isBefore(LocalDateTime.now())) {
//            return "Your token is expired";
//        }
//
//        confirmationTokenService.setConfirmedAt(confirmToken);
//        //SmallTrader smallTrader = smallTraderRepository.findById(confirmationToken.getSmallTraderId()).orElseThrow();
//
//        smallTraderService.enableAppUser(confirmationToken.getSmallTrader().getEmail());
//        return "Congratulation! Your email is confirmed. Now you can end this tab and log in to the app.";
        return null;
    }

    private String buildEmail(String name, String link) {
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
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
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
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
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

}
