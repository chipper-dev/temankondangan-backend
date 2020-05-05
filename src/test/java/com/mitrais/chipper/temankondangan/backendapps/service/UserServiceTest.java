package com.mitrais.chipper.temankondangan.backendapps.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.UserServiceImpl;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@InjectMocks
	UserServiceImpl userService;

	private User user;

	@BeforeEach
	public void init() {

		user = new User(1L, "test@email.com", "12345_", AuthProvider.email, null, null, DataState.ACTIVE);

		Optional<User> userOptional = Optional.of(user);

		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);
		Mockito.when(passwordEncoder.matches("12345_", "12345_")).thenReturn(true);
	}

//	Testing for Change Password API
	@Test
	public void changePasswordTest() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456_", "123456_");
		boolean result = userService.changePassword(1L, wrapper);
		assertTrue(result);
	}

	@Test
	public void shouldThrowBadRequestException_WhenPasswordEmpty() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "", "");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Password cannot be empty!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenOldPasswordNotMatch() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_@", "123456_", "123456_");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Old password do not match!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenOldPasswordSameAsNewPassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "12345_", "12345_");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: New password cannot be the same as old password!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenNewPasswordDoNotMatchWithConfirmedPassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456_", "123457_");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Confirmed password do not match!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenNewPasswordLengthNotRight() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "12_", "12_");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Password length must be 6-20 characters!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenNewPasswordDoNotHaveSpecialChar() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456", "123456");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Password must have at least one special character!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenNewPasswordDoNotHaveDigit() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "ABCDEF_", "ABCDEF_");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Password must have at least one digit character!")
				.isInstanceOf(BadRequestException.class);
	}

	// Testing for Create Password API
	@Test
	public void createPasswordTest() {
		// password must be null
		user.setPasswordHashed(null);
		// provider must be google
		user.setProvider(AuthProvider.google);

		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		boolean result = userService.createPassword(1L, wrapper);
		assertTrue(result);
	}

	@Test
	public void shouldThrowBadRequestException_WhenPasswordIsNotNull() {
		// provider must be google
		user.setProvider(AuthProvider.google);

		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		assertThatThrownBy(() -> userService.createPassword(1L, wrapper))
				.hasMessageContaining("Error: Password has been created").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenProviderIsNotGoogle() {
		// test with other provider that is not google
		user.setPasswordHashed(null);

		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		assertThatThrownBy(() -> userService.createPassword(1L, wrapper))
				.hasMessageContaining("Error: Cannot create password without google provider")
				.isInstanceOf(BadRequestException.class);
	}

	// Testing for Remove User
	@Test
	public void removeUserTest() {
		doAnswer(invocation -> {
			User user = invocation.getArgument(0, User.class);
			user.setDataState(DataState.DELETED);

			return null;
		}).when(userRepository).delete(Mockito.any(User.class));

		userService.remove(1L);
		assertEquals(DataState.DELETED, user.getDataState());
	}
}
