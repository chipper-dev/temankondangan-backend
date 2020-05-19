package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ImageFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
public class ImageFileServiceImpl implements ImageFileService {

	private ProfileRepository profileRepository;

	@Autowired
	public ImageFileServiceImpl(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	public Profile getImageById(String profileIdStr) throws FileNotFoundException {
		Long profileId = Long.parseLong(profileIdStr);

		return profileRepository.findById(profileId)
				.orElseThrow(() -> new FileNotFoundException("File not found with id " + profileId));
	}

	public Profile getImageByFilename(String fileName) throws FileNotFoundException {
		return profileRepository.findByPhotoProfileFilename(fileName)
				.orElseThrow(() -> new FileNotFoundException("File not found with filename: " + fileName));
	}
}
