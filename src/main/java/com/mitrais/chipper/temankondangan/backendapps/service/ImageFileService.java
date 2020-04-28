package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;

import java.io.FileNotFoundException;

public interface ImageFileService {
    Profile getImage(String profileId) throws FileNotFoundException;
}
