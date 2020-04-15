package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	private static String DEFAULT_IMAGE = "image/defaultprofile.jpg";

	@Override
	@Transactional
	public boolean update(ProfileUpdateWrapper wrapper) {
		try {
			Profile profile = profileRepository.findByUserId(wrapper.getUserId())
					.orElseThrow(() -> new NoSuchElementException("No such user"));

			byte[] image;

			if (wrapper.getImage().isEmpty()) {
				image = readBytesFromFile(DEFAULT_IMAGE);
			} else {
				image = wrapper.getImage().getBytes();
			}

			profile.setPhotoProfile(image);
			profile.setModifiedDate(new Date());
			profile.setModifiedBy(wrapper.getFullName());
			profile.setAboutMe(wrapper.getAboutMe());
			profile.setCity(wrapper.getCity());
			profile.setInterest(wrapper.getInterest());
			profile.setDob(wrapper.getDob());
			profile.setGender(wrapper.getGender());
			profileRepository.save(profile);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static byte[] readBytesFromFile(String filePath) {

		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;

		try {

			File file = new File(filePath);
			bytesArray = new byte[(int) file.length()];

			// read file into bytes[]
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytesArray);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return bytesArray;

	}
}
