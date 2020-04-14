package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.BackendAppsApplication;
import com.mitrais.chipper.temankondangan.backendapps.model.Users;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@SpringBootTest
public class RegisterServiceImplTest {

    @Autowired
    RegisterServiceImpl registerService;

    @Test
    public void testRegisteringNewUser() {
        RegisterUserWrapper wrapper = new RegisterUserWrapper();
        wrapper.setEmail("test@example.com");
        wrapper.setPassword("password123");
        wrapper.setConfirmPassword("password123");
        wrapper.setDob(new Date());
        wrapper.setFullname("test");
        wrapper.setGender("L");
        Users user = registerService.save(wrapper);
        Assert.notNull(user.getUserId(), "id is null");
    }

    @Test
    public void testRegisteringNewUserWithDifferentPassword() {
        RegisterUserWrapper wrapper = new RegisterUserWrapper();
        wrapper.setEmail("test2@example.com");
        wrapper.setPassword("password123");
        wrapper.setConfirmPassword("password1234");
        wrapper.setDob(new Date());
        wrapper.setFullname("test2");
        wrapper.setGender("L");
        Assertions.assertThrows(ResponseStatusException.class, () -> {
            registerService.save(wrapper);
        });
    }
}
