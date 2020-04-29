package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.AuthProvider;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.json.OauthResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuthServiceImpl implements OAuthService {

	public static final Logger LOGGER = LoggerFactory.getLogger(OAuthServiceImpl.class);

	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;
	private TokenProvider tokenProvider;
	private UserRepository userRepository;

	@Autowired
	public OAuthServiceImpl(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
			TokenProvider tokenProvider, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
		this.userRepository = userRepository;
	}

	@Override
	public OauthResponseWrapper getToken(String email, String uid) {
		OauthResponseWrapper responseWrapper = new OauthResponseWrapper();

		try {
			UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
			if (userRecord.getEmail().equalsIgnoreCase(email)) {
				responseWrapper = generateResponse(userRecord);
			}
		} catch (FirebaseAuthException e) {
			LOGGER.error("getToken", e);
		}

		return responseWrapper;
	}

	private OauthResponseWrapper generateResponse(UserRecord userRecord) {
		OauthResponseWrapper responseWrapper = new OauthResponseWrapper();
		Optional<User> existUser = userRepository.findByEmail(userRecord.getEmail());

		if (!existUser.isPresent()) {
			saveUser(userRecord);
			responseWrapper.setExist(false);
		} else {
			updateUser(existUser.get(), userRecord);
			responseWrapper.setExist(true);
		}

		responseWrapper.setToken(generateToken(userRecord));
		responseWrapper.setFullName(userRecord.getDisplayName());

		return responseWrapper;
	}

	private String generateToken(UserRecord userRecord) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(userRecord.getEmail(), userRecord.getUid()));

		return tokenProvider.createToken(authentication);
	}

	private User saveUser(UserRecord userRecord) {
		User user = User.builder().email(userRecord.getEmail()).uid(passwordEncoder.encode(userRecord.getUid()))
				.provider(AuthProvider.google).dataState(DataState.ACTIVE).build();

		return userRepository.save(user);
	}

	private void updateUser(User user, UserRecord userRecord) {
		if (!passwordEncoder.matches(userRecord.getUid(), user.getUid())) {
			user.setUid(passwordEncoder.encode(userRecord.getUid()));
			userRepository.save(user);
		}
	}
}
