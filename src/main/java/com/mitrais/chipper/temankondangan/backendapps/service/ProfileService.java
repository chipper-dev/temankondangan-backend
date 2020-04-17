package com.mitrais.chipper.temankondangan.backendapps.service;

import java.util.Optional;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;

public interface ProfileService {

	public boolean update(ProfileUpdateWrapper wrapper);

	public Optional<Profile> findByUserId(Long userId);

}
