package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;

import java.io.IOException;

public interface ProfileService {

	public Profile update(Long userId, ProfileUpdateWrapper wrapper) throws IOException;

	public ProfileResponseWrapper findByUserId(Long userId);

}
