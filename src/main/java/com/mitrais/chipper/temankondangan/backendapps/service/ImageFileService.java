package com.mitrais.chipper.temankondangan.backendapps.service;

import java.io.FileNotFoundException;

import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileMicroservicesDTO;

public interface ImageFileService {
    ProfileMicroservicesDTO getImageById(String header, String profileId) throws FileNotFoundException;
    ProfileMicroservicesDTO getImageByFilename(String fileName) throws FileNotFoundException;
    byte[] readBytesFromFile(String defaultImage);
	String getImageUrl(ProfileMicroservicesDTO profile);
}
