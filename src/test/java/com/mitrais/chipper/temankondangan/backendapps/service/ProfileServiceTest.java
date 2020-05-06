package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateProfileWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfileServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	ProfileRepository profileRepository;

	@InjectMocks
	ProfileServiceImpl profileService;

	private static MultipartFile multipartFile;
	private static ProfileUpdateWrapper wrapper;
	private static CreateProfileWrapper createProfileWrapper;
	private User user;

	@BeforeEach
	public void init() {
		user = new User(1L, "test@email.com", "12345_", null, null, null, DataState.ACTIVE);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);
		Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(userOptional);
	}

	@Test
	public void createProfileSuccessfully() {
		createProfileWrapper = CreateProfileWrapper.builder()
				.dob("01-01-2000")
				.email("test@email.com")
				.fullname("full_name")
				.gender(Gender.L)
				.build();

		Profile result = profileService.create(createProfileWrapper);
		assertEquals("full_name", result.getFullName());
	}

	@Test
	public void createProfileWithAgeIsUnderEighteen() {
		LocalDate today = LocalDate.now();
		String formattedDate = today.format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));

		createProfileWrapper = CreateProfileWrapper.builder()
				.dob(formattedDate)
				.email("test@email.com")
				.fullname("full_name")
				.gender(Gender.L)
				.build();

		assertThatThrownBy(() -> profileService.create(createProfileWrapper))
				.hasMessageContaining("Error: Age should not under 18!");
	}

	@Test
	public void updateProfileTestWithImage() {
		multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());
		Profile profile = new Profile((long) 1, user, "full name test", LocalDate.now(), Gender.L, null, "Klaten city",
				"All about me", "Not interested", DataState.ACTIVE);
		Optional<Profile> profileOptional = Optional.of(profile);
		Mockito.when(profileRepository.findByUserId(Mockito.any(Long.class))).thenReturn(profileOptional);
		Mockito.when(profileRepository.save(Mockito.any(Profile.class))).thenReturn(profile);

		wrapper = new ProfileUpdateWrapper(multipartFile, "Klaten city", "All about me", "Not interested");

		Profile result = profileService.update(1L, wrapper);
		assertEquals("full name test", result.getFullName());
	}

}
