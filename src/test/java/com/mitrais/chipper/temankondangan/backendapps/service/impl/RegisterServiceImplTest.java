package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterServiceImplTest {

	@Autowired
	RegisterServiceImpl registerService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	ProfileRepository profileRepository;

	@BeforeAll
	public void init() {
		Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> {
			User user = i.getArgument(0, User.class);
			user.setUserId(1L);
			return user;
		});
		Mockito.when(profileRepository.save(Mockito.any(Profile.class))).thenAnswer(i -> {
			Profile profile = i.getArgument(0, Profile.class);
			profile.setProfileId(1L);
			return profile;
		});
		;
	}

	@Test
	public void testRegisteringNewUser() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setPassword("password123");
		wrapper.setConfirmPassword("password123");
		wrapper.setDob(new Date());
		wrapper.setFullname("test");
		wrapper.setGender("L");
		User user = registerService.save(wrapper);
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
