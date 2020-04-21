package com.mitrais.chipper.temankondangan.backendapps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.mitrais.chipper.temankondangan.backendapps.model.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.AuthServiceImpl;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.ProfileServiceImpl;

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
		User user = new User(1L, "test@email.com", "12345_", null, null, null);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);

		multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

//		Profile beforeProfile = new Profile((long) 1, user, "full name test", LocalDate.now(), Gender.L, null,
//				"Klaten city", "All about me", "Not interested", "Test", LocalDateTime.now(), "Test",
//				LocalDateTime.now());
//		Profile afterProfile = new Profile((long) 1, user, "full name changed", LocalDate.now(), Gender.L, null,
//				"Klaten city", "All about me", "Not interested", "Test", LocalDateTime.now());

		Profile beforeProfile = new Profile((long) 1, user, "full name test", LocalDate.now(), Gender.L, null,
				"Klaten city", "All about me", "Not interested");
		Profile afterProfile = new Profile((long) 1, user, "full name changed", LocalDate.now(), Gender.L, null,
				"Klaten city", "All about me", "Not interested");

		Optional<Profile> beforeProfileOptional = Optional.of(beforeProfile);

		Mockito.when(profileRepository.findByUserId(Mockito.any(Long.class))).thenReturn(beforeProfileOptional);
		Mockito.when(profileRepository.save(Mockito.any(Profile.class))).thenReturn(afterProfile);

	}

	@Test
	public void UpdateProfileTestWithImage() throws IOException {
		ProfileUpdateWrapper profileWrapper = new ProfileUpdateWrapper(multipartFile, 1L, "full name changed",
				LocalDate.now(), Gender.L, "Klaten city", "All about me", "Not interested");
		Profile result = profileService.update(profileWrapper);
		assertEquals("full name changed", result.getFullName());
	}

}
