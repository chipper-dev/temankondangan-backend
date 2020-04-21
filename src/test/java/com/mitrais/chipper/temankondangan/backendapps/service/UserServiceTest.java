package com.mitrais.chipper.temankondangan.backendapps.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
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

import com.mitrais.chipper.temankondangan.backendapps.model.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
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

	@BeforeEach
	public void init() {
		User user = new User(1L, "test@email.com", passwordEncoder.encode("12345_"), null, null);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);
		Mockito.when(tokenProvider.getUserIdFromToken(Mockito.anyString())).thenReturn(1L);
	}

//	Testing for Change Password API
	@Test
	public void ChangePasswordTest() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456_", "123456_");
		boolean result = userService.changePassword(wrapper, "dummy token");
		assertTrue(result);
	}

	@Test
	public void ShouldThrowResponseStatusException_WhenPasswordEmpty() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "", "");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void ShouldThrowResponseStatusException_WhenOldPasswordNotMatch() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_@", "123456_", "123456_");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void ShouldThrowResponseStatusException_WhenOldPasswordSameAsNewPassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "12345_", "12345_");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void ShouldThrowResponseStatusException_WhenNewPasswordDoNotMatchWithConfirmedPassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456_", "123457_");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void ShouldThrowResponseStatusException_WhenNewPasswordLengthNotRight() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "12_", "12_");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void ShouldThrowResponseStatusException_WhenNewPasswordDoNotHaveSpecialChar() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456", "123456");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void ShouldThrowResponseStatusException_WhenNewPasswordDoNotHaveDigit() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "ABCDEF_", "ABCDEF_");
		assertThatThrownBy(() -> userService.changePassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	// Testing for Create Password API
	@Test
	public void CreatePasswordTest() {
		User user = new User(1L, "test@gmail.com", null, AuthProvider.google, null);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);

		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		boolean result = userService.createPassword(wrapper, "dummy token");
		assertTrue(result);
	}

	@Test
	public void ShouldThrowResponseStatusException_WhenPasswordIsNotNull() {
		User user = new User(1L, "test@gmail.com", passwordEncoder.encode("12345_"), AuthProvider.google, "null");
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);

		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		assertThatThrownBy(() -> userService.createPassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void ShouldThrowResponseStatusException_WhenProviderIsNotGoogle() {
		User user = new User(1L, "test@gmail.com", null, AuthProvider.email, null);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);

		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		assertThatThrownBy(() -> userService.createPassword(wrapper, "dummy token"))
				.isInstanceOf(ResponseStatusException.class);
	}
}
