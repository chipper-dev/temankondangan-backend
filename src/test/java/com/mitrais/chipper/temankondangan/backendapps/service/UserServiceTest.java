package com.mitrais.chipper.temankondangan.backendapps.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.UserServiceImpl;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

	@Autowired
	UserServiceImpl userService;

	@MockBean
	UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	public void init() {
		User user = new User(1L, "test@email.com", passwordEncoder.encode("123"), "test user", new Date(), "test user",
				new Date(), null, null);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);
	}

	@Test
	public void ChangePasswordTest() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper(1L, "123", "12345", "12345");
		boolean result = userService.changePassword(wrapper);
		assertTrue(result);
	}

	@Test
	public void ChangePasswordDifferentOldPassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper(1L, "123Q", "12345", "12345");
		boolean result = userService.changePassword(wrapper);
		assertFalse(result);
	}

	@Test
	public void CreatePasswordTest() {
		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper(1L, "12345", "12345");
		boolean result = userService.createPassword(wrapper);
		assertTrue(result);
	}

	@Test
	public void CreatePasswordDifferentOldPassword() {
		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper(1L, "12345", "12345Q");
		boolean result = userService.createPassword(wrapper);
		assertFalse(result);
	}
}
