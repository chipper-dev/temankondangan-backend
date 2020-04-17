package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthServiceTest {

	@Autowired
	AuthServiceImpl authService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	ProfileRepository profileRepository;

	@BeforeEach
	public void init() {
		Mockito.when(userRepository.existsByEmail("exist@example.com")).thenReturn(true);

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
		wrapper.setPassword("p@ssword123");
		wrapper.setConfirmPassword("p@ssword123");
		wrapper.setDob("10-10-1994");
		wrapper.setFullname("test");
		wrapper.setGender(Gender.L);
		User user = authService.save(wrapper);
		Assert.notNull(user.getUserId(), "id is null");
	}

	@Test
	public void testRegisteringNewUserWithDifferentPassword() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setPassword("p@ssword123");
		wrapper.setConfirmPassword("p@ssword1234");
		wrapper.setDob("10-10-1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
	}

	@Test
	public void testRegisteringNewUserWithAlreadyExistEmail() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("exist@example.com");
		wrapper.setPassword("p@ssword123");
		wrapper.setConfirmPassword("p@ssword123");
		wrapper.setDob("10-10-1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
	}

	@Test
	public void testRegisteringNewUserWithNoPassword() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setDob("10-10-1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
	}

	@Test
	public void testRegisteringNewUserWithPasswordEmptyString() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setPassword("");
		wrapper.setConfirmPassword("");
		wrapper.setDob("10-10-1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
	}

	@Test
	public void testRegisteringNewUserWithWrongDOBFormat() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setPassword("");
		wrapper.setConfirmPassword("");
		wrapper.setDob("10/10/1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
	}

	@Test
	public void testRegisteringNewUserWithNotValidDOB() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setPassword("");
		wrapper.setConfirmPassword("");
		wrapper.setDob("32/10/1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
	}

	@Test
	public void testRegisteringNewUserWithNotValidEmailFormat() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test.example.com");
		wrapper.setPassword("");
		wrapper.setConfirmPassword("");
		wrapper.setDob("32/10/1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
	}

	@Test
	public void testRegisteringNewUserWithNotValidPasswordFormat() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test.example.com");
		wrapper.setPassword("");
		wrapper.setConfirmPassword("");
		wrapper.setDob("32/10/1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
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
		boolean result = authService.login("not.exist@example.com", "password1234");
		Assertions.assertFalse(result);
	}

	@Test
	public void testLoginUsingPasswordNotSet() {
		boolean result = authService.login("test@example.com",null);
		Assertions.assertFalse(result);
	}

}
