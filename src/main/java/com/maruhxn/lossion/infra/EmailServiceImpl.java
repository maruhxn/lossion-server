package com.maruhxn.lossion.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(String email, String message) {
        log.info("이메일 전송 - email: {}, message: {}", email, message);
    }
}
