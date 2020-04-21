package com.mitrais.chipper.temankondangan.backendapps.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.UserServiceImpl;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

	@Autowired
	UserServiceImpl userService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	TokenProvider tokenProvider;

	@Autowired
	PasswordEncoder passwordEncoder;

	private User user;

	@BeforeEach
	public void init() {

		user = new User(1L, "test@email.com", passwordEncoder.encode("12345_"), AuthProvider.email, null, null,
				DataState.ACTIVE);

		Optional<User> userOptional = Optional.of(user);

		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);
		Mockito.when(tokenProvider.getUserIdFromToken(Mockito.anyString())).thenReturn(1L);

	}

//	Testing for Change Password API
	@Test
	public void changePasswordTest() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456_", "123456_");
		boolean result = userService.changePassword(wrapper, "dummy token");
		assertTrue(result);
	}

	@Test
	public void shouldThrowResponseStatusException_WhenPasswordEmpty() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "", "");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void shouldThrowResponseStatusException_WhenOldPasswordNotMatch() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_@", "123456_", "123456_");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void shouldThrowResponseStatusException_WhenOldPasswordSameAsNewPassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "12345_", "12345_");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void shouldThrowResponseStatusException_WhenNewPasswordDoNotMatchWithConfirmedPassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456_", "123457_");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void shouldThrowResponseStatusException_WhenNewPasswordLengthNotRight() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "12_", "12_");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void shouldThrowResponseStatusException_WhenNewPasswordDoNotHaveSpecialChar() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456", "123456");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void shouldThrowResponseStatusException_WhenNewPasswordDoNotHaveDigit() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "ABCDEF_", "ABCDEF_");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	// Testing for Create Password API
	@Test
	public void createPasswordTest() {
		// password must be null
		user.setPasswordHashed(null);
		// provider must be google
		user.setProvider(AuthProvider.google);

		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		boolean result = userService.createPassword(wrapper, "dummy token");
		assertTrue(result);
	}

	@Test
	public void shouldThrowResponseStatusException_WhenPasswordIsNotNull() {
		// provider must be google
		user.setProvider(AuthProvider.google);

		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		assertThatThrownBy(() -> userService.createPassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void shouldThrowResponseStatusException_WhenProviderIsNotGoogle() {
		// test with other provider that is not google

		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		assertThatThrownBy(() -> userService.createPassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	// Testing for Remove User
	@Test
	public void removeUserTest() {
		doAnswer(invocation -> {
			User user = invocation.getArgument(0, User.class);
			user.setDataState(DataState.DELETED);

			return null;
		}).when(userRepository).delete(Mockito.any(User.class));

		userService.remove("dummy token");
		assertEquals(DataState.DELETED, user.getDataState());
	}
}
