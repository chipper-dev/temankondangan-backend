package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;

public interface UserService {

	public boolean changePassword(UserChangePasswordWrapper wrapper);

	public User findById(Long userId);

}
