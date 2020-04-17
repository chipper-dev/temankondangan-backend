package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		User user = new User(1L, "test@email.com", passwordEncoder.encode("123"), "test user", LocalDateTime.now(), "test user",
				LocalDateTime.now(), null, null);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);
		Mockito.when(tokenProvider.getUserIdFromToken(Mockito.anyString())).thenReturn(1L);
	}

	@Test
	public void ChangePasswordTest() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("123", "12345", "12345");
		boolean result = userService.changePassword(wrapper, "dummy token");
		assertTrue(result);
	}

	@Test
	public void ChangePasswordDifferentOldPassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("123Q", "12345", "12345");
		boolean result = userService.changePassword(wrapper, "dummy token");
		assertFalse(result);
	}

	@Test
	public void CreatePasswordTest() {
		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345", "12345");
		boolean result = userService.createPassword(wrapper, "dummy token");
		assertTrue(result);
	}

	@Test
	public void CreatePasswordDifferentOldPassword() {
		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345", "12345Q");
		boolean result = userService.createPassword(wrapper, "dummy token");
		assertFalse(result);
	}
}
