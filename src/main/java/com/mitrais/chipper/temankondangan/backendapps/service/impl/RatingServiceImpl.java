package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.exception.UnauthorizedException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.Rating;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Entity;
import com.mitrais.chipper.temankondangan.backendapps.model.en.RatingType;
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
    public void sendRating(Long eventId, Long userVoterId, RatingWrapper ratingWrapper) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", eventId));
        List<Applicant> acceptedApplicantList = applicantRepository.findByEventIdAccepted(event.getEventId());

        if(ratingWrapper.getRatingType().equals(RatingType.APPLICANT)) {
            // Rating validation for applicant
            if(!event.getUser().getUserId().equals(userVoterId)) {
                throw new UnauthorizedException("Error: Event creator doesn't match!");
            }
            if(!acceptedApplicantList.get(0).getApplicantUser().getUserId().equals(ratingWrapper.getUserId())){
                throw new BadRequestException("Error: Event applicant doesn't match!");
            }
        } else if (ratingWrapper.getRatingType().equals(RatingType.CREATOR)) {
            // Rating validation for event creator
            if(!event.getUser().getUserId().equals(ratingWrapper.getUserId())) {
                throw new UnauthorizedException("Error: Event creator doesn't match!");
            }
            if(!acceptedApplicantList.get(0).getApplicantUser().getUserId().equals(userVoterId)){
                throw new BadRequestException("Error: Event applicant doesn't match!");
            }
        } else {
            throw new BadRequestException("Error: Unknown Rating Type. Please use: APPLICANT or CREATOR");
        }

        Rating rating = Rating.builder()
                .eventId(eventId)
                .userId(ratingWrapper.getUserId())
                .score(ratingWrapper.getScore())
                .build();

        ratingRepository.save(rating);
    }

    private void ratingValidation(List<Applicant> acceptedApplicantList, Event event, RatingWrapper ratingWrapper, Long userVoterId) {
        if(ratingWrapper.getRatingType().equals(RatingType.APPLICANT)) {
            // Rating validation for applicant
            ratingEventValidation(event, userVoterId, ratingWrapper.getUserId(), acceptedApplicantList);
        } else if (ratingWrapper.getRatingType().equals(RatingType.CREATOR)) {
            // Rating validation for event creator
            ratingEventValidation(event, ratingWrapper.getUserId(), userVoterId, acceptedApplicantList);
        } else {
            throw new BadRequestException("Error: Unknown Rating Type. Please use: APPLICANT or CREATOR");
        }
    }

    private void ratingEventValidation(Event event, Long eventCreatorId, Long eventApplicantId, List<Applicant> acceptedApplicantList) {
        if (event.getCancelled()) {
            throw new BadRequestException("Error: Event has been canceled you can't give the rating!");
        }
        if (acceptedApplicantList.isEmpty()) {
            throw new BadRequestException("Error: Event doesn't have a accepted Applicant!");
        }

        if(!event.getUser().getUserId().equals(eventCreatorId)) {
            throw new UnauthorizedException("Error: Event creator doesn't match!");
        }
        if(!acceptedApplicantList.get(0).getApplicantUser().getUserId().equals(eventApplicantId)){
            throw new BadRequestException("Error: Event applicant doesn't match!");
        }
    }
}
