package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    private static final String DEFAULT_IMAGE = "image/defaultprofile.jpg";

    @Override
    @Transactional
    public Profile update(ProfileUpdateWrapper wrapper) throws BadRequestException {

        byte[] image;

        // if null or if not select anything
        if (wrapper.getImage() == null || wrapper.getImage().getSize() == 0) {
            image = readBytesFromFile(DEFAULT_IMAGE);
        } else {
            try {
                image = wrapper.getImage().getBytes();
            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
        Profile profile = profileRepository.findByUserId(wrapper.getUserId())
                .orElseThrow(() -> new BadRequestException("Error: User not found!"));

        profile.setPhotoProfile(image);
        profile.setAboutMe(wrapper.getAboutMe());
        profile.setCity(wrapper.getCity());
        profile.setInterest(wrapper.getInterest());
        profile.setDob(wrapper.getDob());
        profile.setGender(wrapper.getGender());
        profile.setFullName(wrapper.getFullName());

        return profile;

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
    public ProfileResponseWrapper findByUserId(Long userId) throws BadRequestException {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("No profile with user id : " + userId));

        String photoProfileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/imagefile/download/")
                .path(String.valueOf(profile.getProfileId())).toUriString();

        return ProfileResponseWrapper.builder().profileId(profile.getProfileId()).fullName(profile.getFullName())
                .dob(profile.getDob()).gender(profile.getGender()).city(profile.getCity()).aboutMe(profile.getAboutMe())
                .interest(profile.getInterest()).photoProfileUrl(photoProfileUrl).build();
    }
}
