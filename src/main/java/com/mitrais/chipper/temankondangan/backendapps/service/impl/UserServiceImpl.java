package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TokenProvider tokenProvider;

	@Override
	public User findById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
	}

	@Override
	public boolean changePassword(UserChangePasswordWrapper wrapper, String token) {

		Long userId = tokenProvider.getUserIdFromToken(token);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: User not found!"));

		// check if all password field empty
		if (wrapper.getNewPassword() == "" || wrapper.getConfirmNewPassword() == "" || wrapper.getOldPassword() == "") {
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
	public boolean createPassword(UserCreatePasswordWrapper wrapper, String token) {

		Long userId = tokenProvider.getUserIdFromToken(token);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: User not found!"));

		if (!user.getPasswordHashed().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Password has been created");
		}

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

}
