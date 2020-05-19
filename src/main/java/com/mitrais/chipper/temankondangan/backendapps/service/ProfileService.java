package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateProfileWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileCreatorResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;

public interface ProfileService {
	Profile create(CreateProfileWrapper wrapper);
	Profile update(Long userId, ProfileUpdateWrapper wrapper);
	ProfileResponseWrapper findByUserId(Long userId);
	ProfileCreatorResponseWrapper findProfileCreator(Long userId);
}
