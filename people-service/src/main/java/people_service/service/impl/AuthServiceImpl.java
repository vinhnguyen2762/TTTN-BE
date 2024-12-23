package people_service.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import people_service.dto.changePasswordDto.ChangPasswordDto;
import people_service.dto.email.EmailDto;
import people_service.dto.smallTrader.SmallTraderCodeDto;
import people_service.dto.smallTrader.SmallTraderForgetPasswordDto;
import people_service.dto.smallTrader.SmallTraderLocalStorageDto;
import people_service.dto.token.TokenDto;
import people_service.exception.AccountLockedException;
import people_service.exception.FailedException;
import people_service.exception.NotFoundException;
import people_service.model.AuthenticationRequest;
import people_service.model.Employee;
import people_service.model.SmallTrader;
import people_service.model.RegistrationRequest;
import people_service.repository.CustomerRepository;
import people_service.repository.EmployeeRepository;
import people_service.repository.SmallTraderRepository;
import people_service.service.AuthService;
import people_service.service.EmailService;
import people_service.service.RegistrationService;
import people_service.utils.Constants;

import javax.management.relation.Role;
import java.security.SecureRandom;
import java.util.Date;

import static people_service.utils.PasswordHashing.checkPassword;
import static people_service.utils.PasswordHashing.hashPassword;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegistrationService registrationService;
    private final SmallTraderRepository smallTraderRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private static final int OTP_LENGTH = 6;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    public Long register(RegistrationRequest request) {
        Long rs = registrationService.register(request);
        return rs;
    }

    public SmallTraderLocalStorageDto login(AuthenticationRequest request) {
        boolean isExistSmallTrader = smallTraderRepository.findByEmail(request.getEmail()).isPresent();
        boolean isExistEmployee = employeeRepository.findByEmail(request.getEmail()).isPresent();

        if (!isExistSmallTrader && !isExistEmployee) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND, request.getEmail()));
        }

        // if user is small trader
        if (isExistSmallTrader) {
            SmallTrader smallTrader = smallTraderRepository.findByEmail(request.getEmail()).orElseThrow();
            boolean isMatch = checkPassword(request.getPassword(), smallTrader.getPassword());
            if (smallTrader.getStatus() == false) {
                throw new FailedException(String.format(Constants.ErrorMessage.USER_NOT_EXIST, request.getEmail()));
            } else if (smallTrader.getLocked() == true) {
                throw new AccountLockedException(String.format(Constants.ErrorMessage.ACCOUNT_IS_LOCKED, request.getEmail()));
            } else if (!isMatch) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
            }
            String token = generateJwt(smallTrader.getId(), smallTrader.getRole().toString());
            return SmallTraderLocalStorageDto.fromSmallTrader(smallTrader.getId(), token, smallTrader.getRole().name());
        }

        // if user is employee
        else {
            Employee employee = employeeRepository.findByEmail(request.getEmail()).orElseThrow();
            boolean isMatch = checkPassword(request.getPassword(), employee.getPassword());
            if (employee.getStatus() == false) {
                throw new FailedException(String.format(Constants.ErrorMessage.USER_NOT_EXIST, request.getEmail()));
            } else if (employee.getLocked() == true) {
                throw new AccountLockedException(String.format(Constants.ErrorMessage.ACCOUNT_IS_LOCKED, request.getEmail()));
            } else if (!isMatch) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
            }
            String token = generateJwt(employee.getId(), employee.getRole().toString());
            return SmallTraderLocalStorageDto.fromSmallTrader(employee.getId(), token, employee.getRole().name());
        }
    }

    public Long checkCodeEmail(SmallTraderCodeDto smallTraderCodeDto) {
        if (smallTraderCodeDto.code().equals(smallTraderCodeDto.codeReceive())) {
            boolean isExistSmallTrader = smallTraderRepository.findByEmail(smallTraderCodeDto.email()).isPresent();
            boolean isExistEmployee = employeeRepository.findByEmail(smallTraderCodeDto.email()).isPresent();

            if (isExistSmallTrader) {
                SmallTrader smallTrader = smallTraderRepository.findByEmail(smallTraderCodeDto.email()).orElseThrow();
                return smallTrader.getId();
            } else {
                Employee employee = employeeRepository.findByEmail(smallTraderCodeDto.email()).orElseThrow();
                return employee.getId();
            }
        } else {
            throw new FailedException(String.format(Constants.ErrorMessage.VERIFY_CODE_FALSE, smallTraderCodeDto.email()));
        }
    }

    public Long checkEmail(EmailDto email) {
        boolean isExistSmallTrader = smallTraderRepository.findByEmail(email.email()).isPresent();
        boolean isExistEmployee = employeeRepository.findByEmail(email.email()).isPresent();

        if (!isExistSmallTrader && !isExistEmployee) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND, email.email()));
        }

        // if user is small trader
        if (isExistSmallTrader) {
            SmallTrader smallTrader = smallTraderRepository.findByEmail(email.email()).orElseThrow();
            if (smallTrader.getStatus() == false) {
                throw new FailedException(String.format(Constants.ErrorMessage.USER_NOT_EXIST, email.email()));
            } else if (smallTrader.getLocked() == true) {
                throw new AccountLockedException(String.format(Constants.ErrorMessage.ACCOUNT_IS_LOCKED, email.email()));
            }
            return smallTrader.getId();
        }

        // if user is employee
        else {
            Employee employee = employeeRepository.findByEmail(email.email()).orElseThrow();
            if (employee.getStatus() == false) {
                throw new FailedException(String.format(Constants.ErrorMessage.USER_NOT_EXIST, email.email()));
            } else if (employee.getLocked() == true) {
                throw new AccountLockedException(String.format(Constants.ErrorMessage.ACCOUNT_IS_LOCKED, email.email()));
            }
            return employee.getId();
        }
    }

    public String sendCodeToEmail(EmailDto email) {
        String code = generateCode();
        emailService.sendMessageWithAttachment(email.email(), buildEmailCode("Verify your email", code));
        return code;
    }

    public Long changeForgetPassword(ChangPasswordDto smallTraderForgetPasswordDto) {
        boolean isExistSmallTrader = smallTraderRepository.findByEmail(smallTraderForgetPasswordDto.email()).isPresent();
        boolean isExistEmployee = employeeRepository.findByEmail(smallTraderForgetPasswordDto.email()).isPresent();

        if (!isExistSmallTrader && !isExistEmployee) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.USER_NOT_FOUND, smallTraderForgetPasswordDto.email()));
        }
        // if user is small trader
        if (isExistSmallTrader) {
            SmallTrader smallTrader = smallTraderRepository.findByEmail(smallTraderForgetPasswordDto.email()).orElseThrow();
            smallTrader.setPassword(hashPassword(smallTraderForgetPasswordDto.password()));
            smallTraderRepository.saveAndFlush(smallTrader);
            return smallTrader.getId();
        }

        // if user is employee
        else {
            Employee employee = employeeRepository.findByEmail(smallTraderForgetPasswordDto.email()).orElseThrow();
            employee.setPassword(hashPassword(smallTraderForgetPasswordDto.password()));
            employeeRepository.saveAndFlush(employee);
            return employee.getId();
        }

    }

    public Long confirmPassword(SmallTraderForgetPasswordDto smallTraderForgetPasswordDto) {
        SmallTrader smallTrader = smallTraderRepository.findById(smallTraderForgetPasswordDto.id()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.SMALL_TRADER_NOT_FOUND, smallTraderForgetPasswordDto.id()))
        );
        boolean isMatch = checkPassword(smallTraderForgetPasswordDto.newPassword(), smallTrader.getPassword());
        if (smallTrader.getStatus() == false) {
            throw new FailedException(String.format(Constants.ErrorMessage.USER_NOT_EXIST, smallTrader.getEmail()));
        } else if (smallTrader.getLocked() == true) {
            throw new AccountLockedException(String.format(Constants.ErrorMessage.ACCOUNT_IS_LOCKED, smallTrader.getEmail()));
        } else if (!isMatch) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
        }
        return smallTrader.getId();
    }

    public Long confirmPasswordEmployee(SmallTraderForgetPasswordDto smallTraderForgetPasswordDto) {
        Employee employee = employeeRepository.findById(smallTraderForgetPasswordDto.id()).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.EMPLOYEE_NOT_FOUND, smallTraderForgetPasswordDto.id()))
        );
        boolean isMatch = checkPassword(smallTraderForgetPasswordDto.newPassword(), employee.getPassword());
        if (employee.getStatus() == false) {
            throw new FailedException(String.format(Constants.ErrorMessage.USER_NOT_EXIST, employee.getEmail()));
        } else if (employee.getLocked() == true) {
            throw new AccountLockedException(String.format(Constants.ErrorMessage.ACCOUNT_IS_LOCKED, employee.getEmail()));
        } else if (!isMatch) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PASSWORD_NOT_CORRECT));
        }
        return employee.getId();
    }

    public Long checkJWT(TokenDto tokenDto) {
        try {
            // kiểm tra xem token có trong blacklist hay không
            if (JwtBlacklistService.isTokenBlacklisted(tokenDto.token())) {
                return 0L;
            }

            // 1. Thuật toán phải giống như khi tạo token
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            // 2. Tạo đối tượng xác thực
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0") // Xác minh claim `iss`
                    .build();

            // 3. Xác thực token, nếu đúng return 1;
            verifier.verify(tokenDto.token());
            return 1L;
        } catch (JWTVerificationException exception) {
            // Xử lý lỗi xác thực
            return 0L;
        }
    }

    private String generateJwt(Long id, String role) {
        // Lấy thời gian hiện tại
        Date now = new Date();

        // Cộng thêm 1 ngày (24 giờ) vào thời gian hiện tại
        Date expiryDate = new Date(now.getTime() + 86400000);
        // 1. Chọn thuật toán ký (HMAC với secret key)
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        // 2. Tạo token
        String token = JWT.create()
                .withIssuer("auth0") // Thêm claim `iss` (issuer - nơi phát hành)\
                .withClaim("id", id) // Claim id
                .withClaim("role", role) // Claim role
                .withExpiresAt(expiryDate)
                .sign(algorithm); // Ký token

        return token;
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
