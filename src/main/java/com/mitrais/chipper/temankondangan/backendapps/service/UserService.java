package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;

public interface UserService {

	public boolean changePassword(UserChangePasswordWrapper wrapper, String token);

	public User findById(Long userId);

	public boolean createPassword(UserCreatePasswordWrapper wrapper, String token);

}
