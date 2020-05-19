package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;

import java.io.FileNotFoundException;

public interface ImageFileService {
    Profile getImageById(String profileId) throws FileNotFoundException;
    Profile getImageByFilename(String fileName) throws FileNotFoundException;
    byte[] readBytesFromFile(String defaultImage);
}
