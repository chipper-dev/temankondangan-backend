package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ResetPasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;

public interface UserService {

	public boolean changePassword(Long userId, UserChangePasswordWrapper wrapper);

	public User findById(Long userId);

	public boolean createPassword(Long userId, UserCreatePasswordWrapper wrapper);

	public void remove(String header, Long userId);

	void forgotPassword(String email);
	void resetPassword(ResetPasswordWrapper wrapper);

	public void saveMessagingToken(Long userId, String token);

}
