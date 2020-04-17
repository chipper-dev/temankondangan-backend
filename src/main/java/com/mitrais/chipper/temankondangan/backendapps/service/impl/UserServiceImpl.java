package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
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
		try {
			Long userId = tokenProvider.getUserIdFromToken(token);
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new NoSuchElementException("No user with user id " + userId));

			if (passwordEncoder.matches(wrapper.getOldPassword(), user.getPasswordHashed())) {
				if (wrapper.getNewPassword().equals(wrapper.getConfirmNewPassword())) {
					user.setPasswordHashed(passwordEncoder.encode(wrapper.getNewPassword()));
					userRepository.save(user);
					return true;
				}
			}

		} catch (Exception e) {
			System.out.println("Error change password");
		}
		return false;
	}

	@Override
	public boolean createPassword(UserCreatePasswordWrapper wrapper, String token) {
		try {
			Long userId = tokenProvider.getUserIdFromToken(token);
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new NoSuchElementException("No user with user id " + userId));

			if (wrapper.getNewPassword().equals(wrapper.getConfirmNewPassword())) {
				user.setPasswordHashed(passwordEncoder.encode(wrapper.getNewPassword()));
				userRepository.save(user);
				return true;
			}

		} catch (Exception e) {
			System.out.println("Error creating password");
		}
		return false;
	}

}
