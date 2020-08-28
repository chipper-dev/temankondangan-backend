package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.base.Optional;
import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileLegacyResponseDTO;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateProfileWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileCreatorResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ImageFileService;
import com.mitrais.chipper.temankondangan.backendapps.service.ProfileService;
import com.mitrais.chipper.temankondangan.backendapps.service.RatingService;

@Service
public class ProfileServiceImpl implements ProfileService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);

	private final ProfileRepository profileRepository;
	private final UserRepository userRepository;
	ImageFileService imageService;
	RatingService ratingService;

	@Autowired
	public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository,
			ImageFileService imageService, RatingService ratingService) {
		this.userRepository = userRepository;
		this.profileRepository = profileRepository;
		this.imageService = imageService;
		this.ratingService = ratingService;
	}

//	@Override
//	public Profile create(CreateProfileWrapper wrapper) {
//		User user = userRepository.findByEmail(wrapper.getEmail())
//				.orElseThrow(() -> new ResourceNotFoundException("User", "email", wrapper.getEmail()));
//
//		// check dob valid
//		LocalDate dob;
//		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
//		try {
//			dob = LocalDate.parse(wrapper.getDob(), df);
//		} catch (Exception e) {
//			throw new BadRequestException("Error: Date not valid!");
//		}
//
//		// check age over 18
//		if (Period.between(dob, LocalDate.now()).getYears() < 18) {
//			throw new BadRequestException("Error: Age should not under 18!");
//		}
//
//		Profile profile = profileRepository.findByUserId(user.getUserId()).orElse(new Profile());
//		profile.setUser(user);
//		profile.setFullName(wrapper.getFullname());
//		profile.setDob(dob);
//		profile.setGender(wrapper.getGender());
//		profile.setDataState(DataState.ACTIVE);
//		return profileRepository.save(profile);
//	}
//
//	@Override
//	public Profile update(Long userId, ProfileUpdateWrapper wrapper) {
//		Profile profile = profileRepository.findByUserId(userId)
//				.orElseThrow(() -> new BadRequestException("Error: User not found!"));
//
//		String[] allowedFormatImage = { "jpeg", "png", "jpg" };
//		List<String> allowedFormatImageList = Arrays.asList(allowedFormatImage);
//
//		if (wrapper.getImage() != null && !StringUtils.isEmpty(wrapper.getImage().getOriginalFilename())) {
//			byte[] image = null;
//			String fileName = "";
//			// throw error if image format is not allowed
//			String[] imageFormat = wrapper.getImage().getOriginalFilename().split("\\.");
//			if (!allowedFormatImageList.contains(imageFormat[imageFormat.length - 1])) {
//				throw new BadRequestException("Error: Image format not allowed!");
//			}
//			try {
//				image = wrapper.getImage().getBytes();
//				fileName = wrapper.getImage().getOriginalFilename().replaceAll("\\s+", "-");
//			} catch (IOException e) {
//				throw new BadRequestException(e.getMessage());
//			}
//			profile.setPhotoProfile(image);
//			profile.setPhotoProfileFilename(fileName);
//		}
//
//		profile.setAboutMe(wrapper.getAboutMe());
//		profile.setCity(wrapper.getCity());
//		profile.setInterest(wrapper.getInterest());
//
//		return profileRepository.save(profile);
//
//	}
//
//	@Override
//	public ProfileResponseWrapper findByUserId(Long userId) {
//		Profile profile = profileRepository.findByUserId(userId)
//				.orElseThrow(() -> new BadRequestException("No profile with user id : " + userId));
//
//		HashMap<String, Double> rating = ratingService.getUserRating(userId);
//		String photoProfileUrl = imageService.getImageUrl(profile);
//
//		boolean hasPassword = true;
//		if (StringUtils.isEmpty(profile.getUser().getPasswordHashed())) {
//			hasPassword = false;
//		}
//
//		return ProfileResponseWrapper.builder().profileId(profile.getProfileId()).fullName(profile.getFullName())
//				.dob(profile.getDob()).gender(profile.getGender()).city(profile.getCity()).aboutMe(profile.getAboutMe())
//				.interest(profile.getInterest()).photoProfileUrl(photoProfileUrl).email(profile.getUser().getEmail())
//				.hasPassword(hasPassword).ratingData(rating).build();
//	}
//
//	@Override
//	public ProfileCreatorResponseWrapper findOtherPersonProfile(Long userId) {
//		Profile profile = profileRepository.findByUserId(userId)
//				.orElseThrow(() -> new BadRequestException("No profile with user id : " + userId));
//
//		Period period = Period.between(profile.getDob(), LocalDate.now());
//		String age = String.valueOf(period.getYears());
//
//		HashMap<String, Double> rating = ratingService.getUserRating(userId);
//		String photoProfileUrl = imageService.getImageUrl(profile);
//
//		return ProfileCreatorResponseWrapper.builder().fullName(profile.getFullName()).age(age)
//				.gender(profile.getGender()).aboutMe(profile.getAboutMe()).interest(profile.getInterest())
//				.photoProfileUrl(photoProfileUrl).ratingData(rating).build();
//	}

	@Override
	public List<ProfileLegacyResponseDTO> fetchAllProfiles() {
		List<ProfileLegacyResponseDTO> response = new ArrayList<>();
		List<Profile> emptyProfiles = new ArrayList<>();
		profileRepository.fetchAllProfiles().orElse(emptyProfiles).forEach(profile -> {
			response.add(new ProfileLegacyResponseDTO(profile));
		});
		return response;
	}

}
