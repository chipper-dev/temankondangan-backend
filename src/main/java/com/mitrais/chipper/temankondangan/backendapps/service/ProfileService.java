package com.mitrais.chipper.temankondangan.backendapps.service;

import java.io.IOException;
import java.util.Optional;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;

public interface ProfileService {

	public Profile update(ProfileUpdateWrapper wrapper) throws IOException;

	public ProfileResponseWrapper findByUserId(Long userId);

}
