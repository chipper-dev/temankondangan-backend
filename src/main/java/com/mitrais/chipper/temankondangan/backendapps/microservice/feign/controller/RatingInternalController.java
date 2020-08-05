package com.mitrais.chipper.temankondangan.backendapps.microservice.feign.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.chipper.temankondangan.backendapps.service.RatingService;

@RestController
@RequestMapping("/legacy/rating")
public class RatingInternalController {

    @Autowired
    RatingService ratingService;

    @GetMapping("/{userId}")
    public HashMap<String, Double> getUserRating(@PathVariable("userId") Long userId) {
        return ratingService.getUserRating(userId);
    }
}
