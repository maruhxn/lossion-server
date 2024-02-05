package com.maruhxn.lossion.infra.email;

import com.maruhxn.lossion.global.error.ErrorCode;
import com.maruhxn.lossion.global.error.exception.InternalServerException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final String SUBJECT = "[LOSSION] 인증메일입니다.";

    @Override
    public void sendEmail(String email, String payload) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            String htmlContent = getCertificationMessage(payload);

            messageHelper.setTo(email);
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new InternalServerException(ErrorCode.MAIL_FAIL);
        }
    }

    private String getCertificationMessage(String payload) {
        String certificationMessage = "";
        certificationMessage += "<h1 style='text-align: center;'>[LOSSION] 인증메일</h1>";
        certificationMessage += "<h3 style='text-align: center;'>인증 코드: <strong style='font-size: 32px; letter-spacing: 8px;'>" + payload + "</strong></h3>";
        return certificationMessage;
    }
}
