package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.VerificationCode;
import com.mitrais.chipper.temankondangan.backendapps.model.en.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ResetPasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.VerificationCodeRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.EmailService;
import com.mitrais.chipper.temankondangan.backendapps.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	private static final String ERROR_USER_NOT_FOUND = "Error: User not found!";

	@Value("${app.verificationExpirationMsec}")
	Long expiration;

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private VerificationCodeRepository verificationRepository;
	private EmailService emailService;
	private SimpleMailMessage template;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, VerificationCodeRepository verificationRepository, SimpleMailMessage template) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.verificationRepository = verificationRepository;
		this.template = template;
	}

	@Override
	public User findById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
	}

	@Override
	public boolean changePassword(Long userId, UserChangePasswordWrapper wrapper) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_USER_NOT_FOUND));

		// check if old password field empty
		if (StringUtils.isEmpty(wrapper.getOldPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Password cannot be empty!");
		}

		// check if old password matched with password in DB
		if (!passwordEncoder.matches(wrapper.getOldPassword(), user.getPasswordHashed())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Old password do not match!");
		}

		// check if old password same as new password
		if (wrapper.getOldPassword().equals(wrapper.getNewPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Error: New password cannot be the same as old password!");
		}

		this.passwordValidation(wrapper.getNewPassword(), wrapper.getConfirmNewPassword());

		user.setPasswordHashed(passwordEncoder.encode(wrapper.getNewPassword()));
		userRepository.save(user);

		return true;

	}

	@Override
	public boolean createPassword(Long userId, UserCreatePasswordWrapper wrapper) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_USER_NOT_FOUND));

		// check if there is password
		if (!StringUtils.isEmpty(user.getPasswordHashed())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Password has been created");
		}

		// check if provider is google
		if (!user.getProvider().equals(AuthProvider.google)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Error: Cannot create password without google provider");
		}

		this.passwordValidation(wrapper.getNewPassword(), wrapper.getConfirmNewPassword());

		user.setPasswordHashed(passwordEncoder.encode(wrapper.getNewPassword()));
		userRepository.save(user);

		return true;

	}

	private void passwordValidation(String password, String confirmPassword) {

		// check if empty
		if (StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmPassword)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Password cannot be empty!");
		}

		// new password and confirmed password need to be matched
		if (!password.equals(confirmPassword)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Confirmed password do not match!");
		}

		Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Pattern digitCasePatten = Pattern.compile("[0-9 ]");

		if (password.length() < 6 || password.length() > 20) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Error: Password length must be 6-20 characters!");
		}

		if (!specialCharPatten.matcher(password).find()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Error: Password must have at least one special character!");
		}

		if (!digitCasePatten.matcher(password).find()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Error: Password must have at least one digit character!");
		}

	}

	@Override
	public void remove(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_USER_NOT_FOUND));

		userRepository.delete(user);

	}

	@Override
	public void forgotPassword(String email) {
		Integer code = generateRandomCode(6);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Error: Your email is not registered. Please try again"));

		saveCode(user.getEmail(), code);
		sendEmailJob(user.getEmail(), code);
	}

	@Override
	public void resetPassword(ResetPasswordWrapper wrapper) {
		resetPasswordValidation(wrapper);

		VerificationCode verificationCode = verificationRepository.findByCode(wrapper.getVerificationCode())
				.orElseThrow(() -> new RuntimeException("Error: Please input correct verification code from your email"));

		// check verification code is expired?
		if (isCodeValid(verificationCode.getCreatedAt())) {
			User user = userRepository.findByEmail(verificationCode.getEmail())
					.orElseThrow(() -> new RuntimeException("Error: User not Found"));

			// check the new password can't be same with old password
			if (passwordEncoder.matches(wrapper.getNewPassword(), user.getPasswordHashed()))
				throw new RuntimeException("Error: New password must be different from current password");

			// Update password
			user.setPasswordHashed(passwordEncoder.encode(wrapper.getNewPassword()));
			userRepository.save(user);

			// Delete verification code
			deleteCode(user.getEmail());
		} else {
			//Expired
			throw new RuntimeException("Error: Please input correct verification code from your email");
		}
	}

	private Integer generateRandomCode(Integer n) {
		int m = (int) Math.pow(10, (double) n - 1);
		return m + new Random().nextInt(9 * m);
	}

	private void saveCode(String email, Integer code) {
		VerificationCode temp = VerificationCode.builder()
				.code(String.valueOf(code))
				.email(email)
				.createdAt(LocalDateTime.now())
				.build();

		verificationRepository.save(temp);
	}

	private void deleteCode(String email) {
		List<VerificationCode> list = verificationRepository.findByEmail(email);
		if(!list.isEmpty()) {
			list.forEach(verificationCode -> {
				logger.info("Delete code: {}", verificationCode.getCode());
				verificationRepository.delete(verificationCode);
			});
		}
	}

	private void sendEmailJob(String email, Integer code) {
		String subject = "Teman Kondangan - Reset Password";
		String body = String.format(Objects.requireNonNull(template.getText()), email, code);
		emailService.sendMessage(email, subject, body);
	}

	private boolean isCodeValid(LocalDateTime createdAt) {
		Duration duration = Duration.between(createdAt, LocalDateTime.now());

		return duration.getSeconds() * 1000 < expiration;
	}

	private void resetPasswordValidation(ResetPasswordWrapper wrapper) {
		if (org.apache.commons.lang3.StringUtils.isAnyEmpty(wrapper.getVerificationCode(), wrapper.getNewPassword(), wrapper.getConfirmPassword())) {
			throw new RuntimeException("Error: Field can't be blank.");
		}

		if (!wrapper.getNewPassword().equals(wrapper.getConfirmPassword())) {
			throw new RuntimeException("Error: Confirmed password do not match!");
		}

		Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Pattern digitCasePatten = Pattern.compile("[0-9 ]");

		if (wrapper.getNewPassword().length() < 6 || wrapper.getNewPassword().length() > 20) {
			throw new RuntimeException("Error: Password length must be 6-20 characters!");
		}

		if (!specialCharPatten.matcher(wrapper.getNewPassword()).find()) {
			throw new RuntimeException("Error: Password must have at least one special character!");
		}

		if (!digitCasePatten.matcher(wrapper.getNewPassword()).find()) {
			throw new RuntimeException("Error: Password must have at least one digit character!");
		}
	}

}
