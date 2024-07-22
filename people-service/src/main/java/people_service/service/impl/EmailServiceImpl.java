package people_service.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import people_service.exception.FailedException;
import people_service.service.EmailService;
import people_service.utils.Constants;


@Service
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    public void sendMessageWithAttachment(String to, String email) {
        // ...

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("vinh@gmail.com");
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setText(email, true);


            mailSender.send(message);
        } catch (MessagingException e) {
            throw new FailedException(String.format(Constants.ErrorMessage.FAILED_TO_SEND_EMAIL, to));
        }
        // ...
    }
}
