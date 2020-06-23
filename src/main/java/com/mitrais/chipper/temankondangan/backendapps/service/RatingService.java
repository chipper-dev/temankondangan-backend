package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.json.RatingWrapper;

public interface RatingService {
    void sendApplicantRating(Long eventId, Long userCreatorid, RatingWrapper ratingWrapper);
}
