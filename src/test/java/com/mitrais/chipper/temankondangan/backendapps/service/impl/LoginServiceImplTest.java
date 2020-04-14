package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.model.Users;
import com.mitrais.chipper.temankondangan.backendapps.model.json.LoginWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginServiceImplTest {

    @Autowired
    LoginServiceImpl loginService;

    @MockBean
    UserRepository userRepository;

    @BeforeAll
    public void init() {
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPasswordHashed("$2a$10$uP17U46Ewhx5MLLBI7z4tuxhSH0/16jbGKOomfeFbupoCHtY629oe"); //password123

        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));

    }

    @Test
    public void testLoginUsingRightPassword() {
        LoginWrapper request = new LoginWrapper();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        boolean result = loginService.login(request);
        Assertions.assertTrue(result);
    }

}
