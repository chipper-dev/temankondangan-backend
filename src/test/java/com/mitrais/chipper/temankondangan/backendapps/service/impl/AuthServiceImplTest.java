package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.*;
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
public class AuthServiceImplTest {

	@Autowired
	AuthServiceImpl authService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	ProfileRepository profileRepository;

	@BeforeEach
	public void init() {
		Mockito.when(userRepository.existsByEmail("test1@example.com")).thenReturn(true);

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

		User user = new User();
		user.setEmail("test@example.com");
		user.setPasswordHashed("$2a$10$uP17U46Ewhx5MLLBI7z4tuxhSH0/16jbGKOomfeFbupoCHtY629oe"); //password123

		Mockito.when(userRepository.findByEmail(user.getEmail()))
				.thenReturn(Optional.of(user));
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
		User user = authService.save(wrapper);
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
			authService.save(wrapper);
		});
	}

	@Test
	public void testRegisteringNewUserWithAlreadyExistEmail() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test1@example.com");
		wrapper.setPassword("password123");
		wrapper.setConfirmPassword("password123");
		wrapper.setDob(new Date());
		wrapper.setFullname("test2");
		wrapper.setGender("L");
		Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
	}

	@Test
	public void testLoginUsingRightPassword() {
		boolean result = authService.login("test@example.com", "password123");
		Assertions.assertTrue(result);
	}

	@Test
	public void testLoginUsingWrongPassword() {
		boolean result = authService.login("test@example.com", "password1234");
		Assertions.assertFalse(result);
	}

	@Test
	public void testLoginUsingEmailNotFound() {
		boolean result = authService.login("test01@example.com", "password1234");
		Assertions.assertFalse(result);
	}
}
