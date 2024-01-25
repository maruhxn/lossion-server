package com.maruhxn.lossion.util;

import com.maruhxn.lossion.infra.email.EmailService;
import com.maruhxn.lossion.infra.file.FileService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public abstract class IntegrationTestSupport {

    @MockBean
    protected EmailService emailService;

    @MockBean
    protected FileService fileService;
}
