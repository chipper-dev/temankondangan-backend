package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Entity;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ApplicantService;

@Service
public class ApplicantServiceImpl implements ApplicantService {
	private static final String ERROR_EVENT_HAS_FINISHED = "Error: This event has finished already";
	
	private EventRepository eventRepository;
	private ApplicantRepository applicantRepository;

	@Autowired
	public ApplicantServiceImpl(EventRepository eventRepository, ApplicantRepository applicantRepository) {
		this.eventRepository = eventRepository;
		this.applicantRepository = applicantRepository;
	}

	@Override
	public void accept(Long applicantId) {
		Applicant applicant = applicantRepository.findById(applicantId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.APPLICANT.getLabel(), "id", applicantId));
		Event event = eventRepository.findById(applicant.getEvent().getEventId()).orElseThrow(
				() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", applicant.getEvent().getEventId()));

		if ((event.getFinishDateTime() != null && LocalDateTime.now().isAfter(event.getFinishDateTime()))
				|| LocalDateTime.now().isAfter(event.getStartDateTime())) {
			throw new BadRequestException(ERROR_EVENT_HAS_FINISHED);
		}

		if (applicant.getStatus() != null && applicant.getStatus().equals(ApplicantStatus.REJECTED)) {
			throw new BadRequestException("Error: You cannot accept rejected applicant");
		}

		applicant.setStatus(ApplicantStatus.ACCEPTED);
		applicantRepository.save(applicant);
	}

	@Override
	public void cancelAccepted(Long applicantId) {
		Applicant applicant = applicantRepository.findById(applicantId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.APPLICANT.getLabel(), "id", applicantId));
		Event event = eventRepository.findById(applicant.getEvent().getEventId()).orElseThrow(
				() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", applicant.getEvent().getEventId()));

		if ((event.getFinishDateTime() != null && LocalDateTime.now().isAfter(event.getFinishDateTime()))
				|| LocalDateTime.now().isAfter(event.getStartDateTime())) {
			throw new BadRequestException(ERROR_EVENT_HAS_FINISHED);
		}

		if (LocalDateTime.now().isAfter(event.getStartDateTime().minusDays(1))) {
			throw new BadRequestException("Error: You cannot cancel the accepted applicant 24 hours before event started");
		}

		if (applicant.getStatus() != null && !applicant.getStatus().equals(ApplicantStatus.ACCEPTED)) {
			throw new BadRequestException("Error: You cannot cancel non accepted applicant");
		}

		applicant.setStatus(ApplicantStatus.APPLIED);
		applicantRepository.save(applicant);

	}

	@Override
	public void rejectApplicant(Long applicantId) {
		Applicant applicant = applicantRepository.findById(applicantId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.APPLICANT.getLabel(), "id", applicantId));
		Event event = eventRepository.findById(applicant.getEvent().getEventId()).orElseThrow(
				() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", applicant.getEvent().getEventId()));

		if ((event.getFinishDateTime() != null && LocalDateTime.now().isAfter(event.getFinishDateTime()))
				|| LocalDateTime.now().isAfter(event.getStartDateTime())) {
			throw new BadRequestException(ERROR_EVENT_HAS_FINISHED);
		}
		
		if (applicant.getStatus().compareTo(ApplicantStatus.ACCEPTED) == 0) {
			throw new BadRequestException("Error: You cannot reject the accepted applicant");
		}
		
		applicant.setStatus(ApplicantStatus.REJECTED);
		applicantRepository.save(applicant);
		
	}

}
