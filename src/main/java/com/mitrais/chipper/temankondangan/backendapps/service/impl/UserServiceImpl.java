package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User findById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
	}

	@Override
	public boolean changePassword(Long userId, UserChangePasswordWrapper wrapper) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: User not found!"));

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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: User not found!"));

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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: User not found!"));

		userRepository.delete(user);

	}

}
