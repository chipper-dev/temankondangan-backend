package com.mitrais.chipper.temankondangan.backendapps.microservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileMSResponseDTO;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Entity;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;

@Service
public class MicroserviceToLegacy {

	private UserRepository userRepository;

	@Autowired
	MicroserviceToLegacy(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Profile convertFromMSProfile(ProfileMSResponseDTO msProfile) {
		Profile profile = new Profile();
		profile.setAboutMe(msProfile.getAboutMe());
		profile.setCity(msProfile.getCity());
		profile.setCreatedBy(msProfile.getCreatedBy());
		profile.setCreatedDate(msProfile.getCreatedDate());
		profile.setDataState(msProfile.isDeleted() ? DataState.ACTIVE : DataState.DELETED);
		profile.setDob(msProfile.getDob());
		profile.setFullName(msProfile.getFullName());
		profile.setGender(msProfile.getGender());
		profile.setInterest(msProfile.getInterest());
		profile.setLastModifiedBy(msProfile.getLastModifiedBy());
		profile.setLastModifiedDate(msProfile.getLastModifiedDate());
		profile.setPhotoProfile(msProfile.getPhotoProfile());
		profile.setPhotoProfileFilename(msProfile.getPhotoProfileFilename());
		profile.setProfileId(msProfile.getId());
		profile.setUser(userRepository.findById(msProfile.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", msProfile.getUserId())));
		return profile;

	}
}
