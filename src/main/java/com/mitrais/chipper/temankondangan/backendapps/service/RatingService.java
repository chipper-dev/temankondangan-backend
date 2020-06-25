package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.json.RatingWrapper;

public interface RatingService {
    void sendRating(Long eventId, Long userCreatorid, RatingWrapper ratingWrapper);
}
