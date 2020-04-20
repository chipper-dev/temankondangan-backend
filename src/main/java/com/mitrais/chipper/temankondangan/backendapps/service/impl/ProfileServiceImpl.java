package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	private static String DEFAULT_IMAGE = "image/defaultprofile.jpg";

	@Override
	@Transactional
	public boolean update(ProfileUpdateWrapper wrapper) {

		try {
			byte[] image;

			if (wrapper.getImage() == null) {
				image = readBytesFromFile(DEFAULT_IMAGE);
			} else {
				image = wrapper.getImage().getBytes();
			}
			Profile profile = profileRepository.findByUserId(wrapper.getUserId())
					.orElseThrow(() -> new NoSuchElementException("No profile with user id : " + wrapper.getUserId()));

			profile.setPhotoProfile(image);
//			profile.setModifiedDate(LocalDateTime.now());
//			profile.setModifiedBy(wrapper.getFullName());
			profile.setAboutMe(wrapper.getAboutMe());
			profile.setCity(wrapper.getCity());
			profile.setInterest(wrapper.getInterest());
			profile.setDob(wrapper.getDob());
			profile.setGender(wrapper.getGender());

			profileRepository.save(profile);

			return true;
		} catch (Exception e) {
			System.out.println("Profile not updated.");
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

	@Override
	public Optional<Profile> findByUserId(Long userId) {
		return profileRepository.findByUserId(userId);
	}
}
