package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
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
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import javax.validation.constraints.AssertTrue;

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

		Mockito.when(userRepository.findById(Mockito.anyLong()))
				.thenReturn(Optional.of(user));
	}

	@Test
	public void testRegisteringNewUser() {
		int day = LocalDate.now().getDayOfMonth();
		int month = LocalDate.now().getMonthValue();
		int year = LocalDate.now().getYear() - 18;
		String dob = String.format("%02d",day) + "-" + String.format("%02d",month) + "-" + year;

		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setPassword("p@ssword123");
		wrapper.setConfirmPassword("p@ssword123");
		wrapper.setDob(dob);
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
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Password and Confirm Password not match!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
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
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Email is already exist!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
	}

	@Test
	public void testRegisteringNewUserWithNoPassword() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setDob("10-10-1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Password cannot empty!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
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
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Password cannot empty!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
	}

	@Test
	public void testRegisteringNewUserWithWrongDOBFormat() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setPassword("p@ssword123");
		wrapper.setConfirmPassword("p@ssword123");
		wrapper.setDob("10/10/1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Date not valid!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
	}

	@Test
	public void testRegisteringNewUserWithNotValidDOBDate() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setPassword("p@ssword123");
		wrapper.setConfirmPassword("p@ssword123");
		wrapper.setDob("32-10-1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Date not valid!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
	}

	@Test
	public void testRegisteringNewUserDOBAgeUnder18() {
		//set dob dynamic 1 day before 18
		int day = LocalDate.now().getDayOfMonth() +1;
		int month = LocalDate.now().getMonthValue();
		int year = LocalDate.now().getYear() - 18;
		String dob = String.format("%02d",day) + "-" + String.format("%02d",month) + "-" + year;

		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setPassword("p@ssword123");
		wrapper.setConfirmPassword("p@ssword123");
		wrapper.setDob(dob);
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Age should not under 18!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
	}

	@Test
	public void testRegisteringNewUserWithNotValidEmailFormat() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test.example.com");
		wrapper.setPassword("p@ssword123");
		wrapper.setConfirmPassword("p@ssword123");
		wrapper.setDob("10-10-1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Email not valid!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
	}

	@Test
	public void testRegisteringNewUserWithNoDomainEmailFormat() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@.com");
		wrapper.setPassword("p@ssword123");
		wrapper.setConfirmPassword("p@ssword123");
		wrapper.setDob("10-10-1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Email not valid!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
	}
	@Test
	public void testRegisteringNewUserWithNoTLDEmailFormat() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example");
		wrapper.setPassword("p@ssword123");
		wrapper.setConfirmPassword("p@ssword123");
		wrapper.setDob("10-10-1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Email not valid!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
	}

	@Test
	public void testRegisteringNewUserWithNotValidPasswordFormat() {
		RegisterUserWrapper wrapper = new RegisterUserWrapper();
		wrapper.setEmail("test@example.com");
		wrapper.setPassword("password");
		wrapper.setConfirmPassword("password");
		wrapper.setDob("10-10-1994");
		wrapper.setFullname("test2");
		wrapper.setGender(Gender.L);
		ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class, () -> {
			authService.save(wrapper);
		});
		String expectedMessage = "Error: Password pattern not valid!";
		Assert.isTrue(expectedMessage.equalsIgnoreCase(e.getReason()),
				expectedMessage + " != " + e.getReason());
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

	@Test
	public void testLogout() {
		boolean result = authService.logout(1L);
		Assertions.assertTrue(result);
	}

}
