package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.util.Date;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.Users;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	private Profile profile;

	ProfileServiceImpl() {

	}

	@Override
	public Profile getProfile(Long userId) {
		return profileRepository.findByUserId(userId).get();
	}

	@Override
	@Transactional
	public boolean update(ProfileUpdateWrapper wrapper) {

		try {
			Users user = userRepository.findById(wrapper.getUserId())
					.orElseThrow(() -> new NoSuchElementException("Invalid account"));
			
			profile = profileRepository.save(new Profile(user, wrapper.getFullName(), wrapper.getDob(),
					wrapper.getGender(), wrapper.getFullName(), new Date(), wrapper.getFullName(), new Date()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
//	@Override
//	public Profile uploadImage(MultipartFile file, Profile profile) {
//		Profile savedProfile = new Profile();
//		try {
//			byte[] image = file.getBytes();
//			profile.setPhotoProfile(image);
//			savedProfile = profileRepository.save(profile);
//
//		} catch (Exception e) {
//
//		}
//		return savedProfile;
//	}

//	public Profile findById()

}
