package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ImageFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class ImageFileServiceImpl implements ImageFileService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ImageFileServiceImpl.class);

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

    public byte[] readBytesFromFile(String filePath) {
        File file = new File(filePath);
        byte[] bytesArray = new byte[(int) file.length()];

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            if (fileInputStream.read(bytesArray) < 0) {
				throw new BadRequestException("Error image file!");
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found");
        } catch (IOException e) {
            LOGGER.error("Something wrong when reading file: {}", e.getLocalizedMessage());
        }

        return bytesArray;
    }

    @Override
    public String getImageUrl(Profile profile) {
        String photoProfileUrl = "";
        if (profile.getPhotoProfile() != null && StringUtils.isNotEmpty(profile.getPhotoProfileFilename())) {
            photoProfileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/imagefile/download/")
                    .path(String.valueOf(profile.getPhotoProfileFilename())).toUriString();
        }
        return photoProfileUrl;
    }
}
