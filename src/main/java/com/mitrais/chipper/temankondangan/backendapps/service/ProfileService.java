package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;

import java.io.IOException;

public interface ProfileService {
	Profile update(ProfileUpdateWrapper wrapper) throws BadRequestException;
	ProfileResponseWrapper findByUserId(Long userId) throws BadRequestException;
}
