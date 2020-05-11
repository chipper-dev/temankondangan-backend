package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ResetPasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;

public interface UserService {

	public boolean changePassword(Long userId, UserChangePasswordWrapper wrapper) throws BadRequestException, ResourceNotFoundException;

	public User findById(Long userId) throws ResourceNotFoundException;

	public boolean createPassword(Long userId, UserCreatePasswordWrapper wrapper) throws BadRequestException;

	public void remove(Long userId) throws BadRequestException;

	void forgotPassword(String email) throws BadRequestException, ResourceNotFoundException;
	void resetPassword(ResetPasswordWrapper wrapper) throws BadRequestException;

}
