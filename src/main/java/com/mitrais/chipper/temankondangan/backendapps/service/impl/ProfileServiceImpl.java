package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateProfileWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileCreatorResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);
	private static final String DEFAULT_IMAGE = "image/defaultprofile.jpg";

	private final ProfileRepository profileRepository;
	private final UserRepository userRepository;

	@Autowired
	public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository) {
		this.userRepository = userRepository;
		this.profileRepository = profileRepository;
	}

	@Override
	public Profile create(CreateProfileWrapper wrapper) {
		User user = userRepository.findByEmail(wrapper.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", wrapper.getEmail()));

		// check dob valid
		LocalDate dob;
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
		try {
			dob = LocalDate.parse(wrapper.getDob(), df);
		} catch (Exception e) {
			throw new BadRequestException("Error: Date not valid!");
		}

		// check age over 18
		if (Period.between(dob, LocalDate.now()).getYears() < 18) {
			throw new BadRequestException("Error: Age should not under 18!");
		}

		byte[] image = readBytesFromFile(DEFAULT_IMAGE);
		String fileName = DEFAULT_IMAGE.split("/")[1];

		Profile profile = profileRepository.findByUserId(user.getUserId()).orElse(new Profile());
		profile.setUser(user);
		profile.setFullName(wrapper.getFullname());
		profile.setDob(dob);
		profile.setGender(wrapper.getGender());
		profile.setPhotoProfile(image);
		profile.setPhotoProfileFilename(fileName);
		profile.setDataState(DataState.ACTIVE);
		return profileRepository.save(profile);
	}

	@Override
	public Profile update(Long userId, ProfileUpdateWrapper wrapper) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new BadRequestException("Error: User not found!"));

		byte[] image = null;
		String fileName = "";

		String[] allowedFormatImage = { "jpeg", "png", "jpg" };
		List<String> allowedFormatImageList = Arrays.asList(allowedFormatImage);

		// if null or if not select anything
		if (wrapper.getImage() == null && profile.getPhotoProfile() == null) {
			image = readBytesFromFile(DEFAULT_IMAGE);
			fileName = DEFAULT_IMAGE.split("/")[1];
		} else if (wrapper.getImage() != null && !StringUtils.isEmpty(wrapper.getImage().getOriginalFilename())) {
			// throw error if image format is not allowed
			String[] imageFormat = wrapper.getImage().getOriginalFilename().split("\\.");
			if (!allowedFormatImageList.contains(imageFormat[imageFormat.length - 1])) {
				throw new BadRequestException("Error: Image format not allowed!");
			}
			try {
				image = wrapper.getImage().getBytes();
				fileName = wrapper.getImage().getOriginalFilename().replaceAll("\\s+", "-");
			} catch (IOException e) {
				throw new BadRequestException(e.getMessage());
			}
		}

		profile.setPhotoProfile(image);
		profile.setPhotoProfileFilename(fileName);
		profile.setAboutMe(wrapper.getAboutMe());
		profile.setCity(wrapper.getCity());
		profile.setInterest(wrapper.getInterest());

		return profileRepository.save(profile);

	}

	private static byte[] readBytesFromFile(String filePath) {

		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;

		try {

			File file = new File(filePath);
			bytesArray = new byte[(int) file.length()];

			// read file into bytes[]
			fileInputStream = new FileInputStream(file);
			if (!(fileInputStream.read(bytesArray) > 0)) {
				throw new BadRequestException("Error image file!");
			}
			fileInputStream.read(bytesArray);

		} catch (IOException e) {
			throw new BadRequestException("Error image file!");
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					LOGGER.error("readBytesFromFile", e);
				}
			}

		}

		return bytesArray;

	}

	@Override
	public ProfileResponseWrapper findByUserId(Long userId) {
		String photoProfileUrl = "";
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new BadRequestException("No profile with user id : " + userId));

		if (profile.getPhotoProfile() != null) {
			photoProfileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/imagefile/download/")
					.path(String.valueOf(profile.getProfileId())).toUriString();
		}

		boolean hasPassword = true;
		if (StringUtils.isEmpty(profile.getUser().getPasswordHashed())) {
			hasPassword = false;
		}

		return ProfileResponseWrapper.builder().profileId(profile.getProfileId()).fullName(profile.getFullName())
				.dob(profile.getDob()).gender(profile.getGender()).city(profile.getCity()).aboutMe(profile.getAboutMe())
				.interest(profile.getInterest()).photoProfileUrl(photoProfileUrl).email(profile.getUser().getEmail())
				.hasPassword(hasPassword).build();
	}

	@Override
	public ProfileCreatorResponseWrapper findProfileCreator(Long userId) {
		String photoProfileUrl = "";
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new BadRequestException("No profile with user id : " + userId));

		Period period = Period.between(profile.getDob(), LocalDate.now());
		String age = String.valueOf(period.getYears());

		if (profile.getPhotoProfile() != null) {
			photoProfileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/imagefile/download/")
					.path(String.valueOf(profile.getPhotoProfileFilename())).toUriString();
		}

		return ProfileCreatorResponseWrapper.builder().fullName(profile.getFullName()).age(age)
				.gender(profile.getGender()).aboutMe(profile.getAboutMe()).interest(profile.getInterest())
				.photoProfileUrl(photoProfileUrl).build();
	}

}
