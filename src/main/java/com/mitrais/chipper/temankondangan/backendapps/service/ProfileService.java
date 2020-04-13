package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;

public interface ProfileService {

	public Profile getProfile(Long userId);

	public boolean update(ProfileUpdateWrapper wrapper);

//	public Profile uploadImage(MultipartFile file);

//	public Profile uploadImage(MultipartFile file, Profile profile);

}
