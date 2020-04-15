package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User findById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
	}

	@Override
	public boolean changePassword(UserChangePasswordWrapper wrapper) {
		try {
			User user = userRepository.findById(wrapper.getUserId())
					.orElseThrow(() -> new NoSuchElementException("No user with user id " + wrapper.getUserId()));
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

}
