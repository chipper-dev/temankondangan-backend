package com.mitrais.chipper.temankondangan.backendapps.microservice.feign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.service.ImageFileService;

@RestController
@RequestMapping("/legacy/image")
public class ImageInternalController {

    @Autowired
    ImageFileService imageFileService;

    @PostMapping("/url")
    public String getImageUrl(@RequestBody Profile profile) {
        return imageFileService.getImageUrl(profile);
    }
}
