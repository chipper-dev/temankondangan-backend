package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.json.RatingWrapper;

import java.util.HashMap;

public interface RatingService {
    void sendRating(Long eventId, Long userCreatorid, RatingWrapper ratingWrapper);
    HashMap<String, Double> getUserRating(Long userId);
    boolean isRated(Long userId, Long eventId);
}
