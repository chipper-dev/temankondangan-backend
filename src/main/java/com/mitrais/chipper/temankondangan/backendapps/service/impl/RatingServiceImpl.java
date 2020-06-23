package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.exception.UnauthorizedException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.Rating;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Entity;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RatingWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.RatingRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    RatingRepository ratingRepository;
    EventRepository eventRepository;
    ApplicantRepository applicantRepository;
    ProfileRepository profileRepository;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, EventRepository eventRepository, ApplicantRepository applicantRepository, ProfileRepository profileRepository) {
        this.ratingRepository = ratingRepository;
        this.eventRepository = eventRepository;
        this.applicantRepository = applicantRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public void sendApplicantRating(Long eventId, Long userCreatorId, RatingWrapper ratingWrapper) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", eventId));
        List<Applicant> acceptedApplicantList = applicantRepository.findByEventIdAccepted(event.getEventId());
        rateValidation(event, userCreatorId, ratingWrapper.getUserId(), acceptedApplicantList);

        Rating rating = Rating.builder()
                .eventId(eventId)
                .userId(ratingWrapper.getUserId())
                .score(ratingWrapper.getScore())
                .build();



        ratingRepository.save(rating);
    }

    private void rateValidation(Event event, Long userVoter, Long userId, List<Applicant> acceptedApplicantList) {
        if(!event.getUser().getUserId().equals(userVoter)) {
            throw new UnauthorizedException("Error: You aren't the creator of the event!");
        }
        if (event.getCancelled()) {
            throw new BadRequestException("Error: Event has been canceled you can't give the rating!");
        }
        if (acceptedApplicantList.isEmpty()) {
            throw new BadRequestException("Error: Event doesn't have a accepted Applicant!");
        }
        if(!acceptedApplicantList.get(0).getApplicantUser().getUserId().equals(userId)){
            throw new BadRequestException("Error: Event applicant doesn't match!");
        }
    }
}
