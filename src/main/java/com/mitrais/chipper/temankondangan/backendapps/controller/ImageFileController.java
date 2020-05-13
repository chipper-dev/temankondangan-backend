package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.service.ImageFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/imagefile")
public class ImageFileController extends CommonResource {
    @Autowired
    ImageFileService service;

    @GetMapping("/download/{profileId}")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request, @PathVariable String profileId) {
        try {
            Profile dbFile = service.getImage(profileId);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new ByteArrayResource(dbFile.getPhotoProfile()));
        } catch (Exception ex) {
            LOGGER.error("downloadFile",ex);
            return ResponseEntity.badRequest()
                    .body(null);
        }

    }
}
