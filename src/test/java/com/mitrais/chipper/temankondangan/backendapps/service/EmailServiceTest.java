package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.service.impl.EmailServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailServiceTest {
    @Autowired
    EmailServiceImpl emailService;

    @Test
    public void testSendEmail() {
        emailService.sendMessage("<your gmail address>@gmail.com", "Test Send Email", "Hello Hooman!");
    }
}
