package com.mitrais.chipper.temankondangan.backendapps.microservice.feign.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileLegacyResponseDTO;
import com.mitrais.chipper.temankondangan.backendapps.service.ProfileService;

@RestController
@RequestMapping("/legacy/db")
public class GetLegacyDataInternalController {

    @Autowired
    ProfileService profileService;

    @GetMapping("/allprofiles")
    public List<ProfileLegacyResponseDTO> getAllProfiles() {
        return profileService.fetchAllProfiles();
    }
}
