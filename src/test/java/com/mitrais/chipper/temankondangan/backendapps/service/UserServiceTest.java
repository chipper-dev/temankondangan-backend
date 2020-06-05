package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.VerificationCode;
import com.mitrais.chipper.temankondangan.backendapps.model.en.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ResetPasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.*;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	EventRepository eventRepository;

	@Mock
	ProfileRepository profileRepository;
	
	@Mock
	ApplicantRepository applicantRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	VerificationCodeRepository verificationRepository;

	@Mock
	EmailService emailService;

	@InjectMocks
	UserServiceImpl userService;

	private User user;

	@BeforeEach
	public void init() {

		user = new User(1L, "test@email.com", "12345_", AuthProvider.email, null, null, DataState.ACTIVE);

		Optional<User> userOptional = Optional.of(user);

		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);
		Mockito.when(passwordEncoder.matches("12345_", "12345_")).thenReturn(true);

		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(null);
		Mockito.when(profileRepository.findById(Mockito.any(Long.class))).thenReturn(null);

	}

//	Testing for Change Password API
	@Test
	public void changePasswordTest() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456_", "123456_");
		boolean result = userService.changePassword(1L, wrapper);
		assertTrue(result);
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenUserNotFoundInChangePassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456_", "123456_");
		Mockito.when(userRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper)).isInstanceOf(ResourceNotFoundException.class);
	}
	
	@Test
	public void shouldThrowBadRequestException_WhenPasswordEmptyInChangePassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "", "");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Password cannot be empty!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenOldPasswordNotMatchInChangePassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_@", "123456_", "123456_");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Old password do not match!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenOldPasswordSameAsNewPasswordInChangePassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "12345_", "12345_");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: New password cannot be the same as old password!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenNewPasswordDoNotMatchWithConfirmedPasswordInChangePassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456_", "123457_");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Confirmed password do not match!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenNewPasswordLengthNotRightInChangePassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "12_", "12_");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Password length must be 6-20 characters!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenNewPasswordDoNotHaveSpecialCharInChangePassword() {
		UserChangePasswordWrapper wrapper = new UserChangePasswordWrapper("12345_", "123456", "123456");
		assertThatThrownBy(() -> userService.changePassword(1L, wrapper))
				.hasMessageContaining("Error: Password must have at least one special character!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenNewPasswordDoNotHaveDigitInChangePassword() {
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
	public void shouldThrowResourceNotFoundException_WhenUserNotFoundInCreatePassword() {
		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		Mockito.when(userRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> userService.createPassword(1L, wrapper)).isInstanceOf(ResourceNotFoundException.class);
	}
	
	@Test
	public void shouldThrowBadRequestException_WhenPasswordIsNotNullInCreatePassword() {
		// provider must be google
		user.setProvider(AuthProvider.google);

		UserCreatePasswordWrapper wrapper = new UserCreatePasswordWrapper("12345_", "12345_");
		assertThatThrownBy(() -> userService.createPassword(1L, wrapper))
				.hasMessageContaining("Error: Password has been created").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenProviderIsNotGoogleInCreatePassword() {
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

		Event event = new Event();
		event.setEventId(1L);
		List<Event> events = new ArrayList<Event>();
		events.add(event);
		Mockito.when(eventRepository.findByUserId(anyLong())).thenReturn(Optional.of(events));
		
		Applicant applicant = new Applicant();
		List<Applicant> applicants = new ArrayList<Applicant>();
		applicants.add(applicant);
		Mockito.when(applicantRepository.findByEventId(anyLong())).thenReturn(Optional.of(applicants));
		
		Mockito.doNothing().when(applicantRepository).deleteAll(applicants);
		Mockito.doNothing().when(eventRepository).deleteAll(events);
		
		userService.remove(1L);
		assertEquals(DataState.DELETED, user.getDataState());
	}
	
	@Test
	public void shouldThrowResourceNotFoundException_WhenUserNotFoundInRemoveUser() {
		
		Mockito.when(userRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> userService.remove(1L)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void forgotPasswordTest() {
		VerificationCode verify = new VerificationCode();
		verify.setEmail(user.getEmail());
		verify.setCode("12345");

		List<VerificationCode> list = new ArrayList<>();
		list.add(verify);

		SimpleMailMessage template = new SimpleMailMessage();
		template.setText("text");

		Mockito.when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
		Mockito.when(verificationRepository.findByEmail(any(String.class))).thenReturn(list);
		ReflectionTestUtils.setField(userService, "template", template);

		userService.forgotPassword("test@example.com");

		verify(verificationRepository, times(1)).delete(any(VerificationCode.class));
		verify(verificationRepository, times(1)).save(any(VerificationCode.class));
		verify(emailService, times(1)).sendMessage(any(String.class), any(String.class), any(String.class));
	}

	@Test
	public void resetPasswordTest() {
		ResetPasswordWrapper wrapper = new ResetPasswordWrapper();
		wrapper.setNewPassword("password123_");
		wrapper.setConfirmPassword("password123_");
		wrapper.setVerificationCode("11234");

		VerificationCode verify = new VerificationCode();
		verify.setEmail(user.getEmail());
		verify.setCreatedAt(LocalDateTime.now().minusMinutes(1));
		verify.setCode("11234");

		Mockito.when(verificationRepository.findByCode(anyString())).thenReturn(Optional.of(verify));
		Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		ReflectionTestUtils.setField(userService, "expiration", (long) 300000);

		userService.resetPassword(wrapper);
		verify(userRepository, times(1)).save(any(User.class));
	}
}
