package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ImageFileService;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
@Log
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
		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;

		try {

			File file = new File(filePath);
			bytesArray = new byte[(int) file.length()];

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
}
