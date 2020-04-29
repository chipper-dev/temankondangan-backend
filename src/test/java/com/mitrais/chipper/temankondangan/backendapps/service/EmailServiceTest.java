package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailServiceTest {
    @Autowired
    EmailServiceImpl emailService;

    @Test
    public void testSendEmail() {
        emailService.sendMessage("<your gmail address>@gmail.com", "Test Send Email", "Hello Hooman!");
    }
}
