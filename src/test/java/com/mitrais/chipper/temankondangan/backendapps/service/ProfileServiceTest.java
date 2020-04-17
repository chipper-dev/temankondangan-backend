package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.AuthServiceImpl;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfileServiceTest {

	@Autowired
    AuthServiceImpl registerService;

	@Autowired
	ProfileServiceImpl profileService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	ProfileRepository profileRepository;

	private static MultipartFile multipartFile;

	@BeforeAll
	public void init() {
		User user = new User(1L, "test@email.com", "123", "test user", LocalDateTime.now(), "test user", LocalDateTime.now(), null, null);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);

		multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());
	}

	@Test
	public void UpdateProfileTestWithImage() {
		ProfileUpdateWrapper profileWrapper = new ProfileUpdateWrapper(multipartFile, 1L, "test name", LocalDate.now(), Gender.L,
				"Test city", "Test about me", "Test interest");
		boolean result = profileService.update(profileWrapper);
		assertTrue(result);
	}

}
