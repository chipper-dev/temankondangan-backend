package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
	private static final String ERROR_NOT_CREATOR = "Error: Non event creator cannot do this";

	private EventRepository eventRepository;
	private ApplicantRepository applicantRepository;

	@Autowired
	public ApplicantServiceImpl(EventRepository eventRepository, ApplicantRepository applicantRepository) {
		this.eventRepository = eventRepository;
		this.applicantRepository = applicantRepository;
	}

	private Map<String, Object> checkApplicant(Long userId, Long applicantId) {
		
		Applicant applicant = applicantRepository.findById(applicantId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.APPLICANT.getLabel(), "id", applicantId));
		Event event = eventRepository.findById(applicant.getEvent().getEventId()).orElseThrow(
				() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", applicant.getEvent().getEventId()));

		if (!userId.equals(event.getUser().getUserId())) {
			throw new BadRequestException(ERROR_NOT_CREATOR);
		}
		
		if ((event.getFinishDateTime() != null && LocalDateTime.now().isAfter(event.getFinishDateTime()))
				|| LocalDateTime.now().isAfter(event.getStartDateTime())) {
			throw new BadRequestException(ERROR_EVENT_HAS_FINISHED);
		}
		
		Map<String, Object> result = new HashMap<>();
		result.put(Entity.APPLICANT.getLabel(), applicant);
		result.put(Entity.EVENT.getLabel(), event);
		return result;
	}
	
	@Override
	public void accept(Long userId, Long applicantId) {
		Map<String, Object> checkResult = checkApplicant(userId, applicantId);
		Applicant applicant = (Applicant) checkResult.get(Entity.APPLICANT.getLabel());
		Event event = (Event) checkResult.get(Entity.EVENT.getLabel());

		if (applicant.getStatus() != null && applicant.getStatus().equals(ApplicantStatus.REJECTED)) {
			throw new BadRequestException("Error: You cannot accept rejected applicant");
		}

		if (Boolean.TRUE.equals(applicantRepository.existsByEventAndStatus(event, ApplicantStatus.ACCEPTED))) {
			throw new BadRequestException("Error: You already have accepted applicant");
		}

		applicant.setStatus(ApplicantStatus.ACCEPTED);
		applicantRepository.save(applicant);
	}

	@Override
	public void cancelAccepted(Long userId, Long applicantId) {
		Map<String, Object> checkResult = checkApplicant(userId, applicantId);
		Applicant applicant = (Applicant) checkResult.get(Entity.APPLICANT.getLabel());
		Event event = (Event) checkResult.get(Entity.EVENT.getLabel());

		if (LocalDateTime.now().isAfter(event.getStartDateTime().minusDays(1))) {
			throw new BadRequestException(
					"Error: You cannot cancel the accepted applicant 24 hours before event started");
		}

		if (applicant.getStatus() != null && !applicant.getStatus().equals(ApplicantStatus.ACCEPTED)) {
			throw new BadRequestException("Error: You cannot cancel non accepted applicant");
		}

		applicant.setStatus(ApplicantStatus.APPLIED);
		applicantRepository.save(applicant);

	}
	
	@Override
	public void rejectApplicant(Long userId, Long applicantId) {
		Map<String, Object> checkResult = checkApplicant(userId, applicantId);
		Applicant applicant = (Applicant) checkResult.get(Entity.APPLICANT.getLabel());
		
		if (applicant.getStatus().compareTo(ApplicantStatus.ACCEPTED) == 0) {
			throw new BadRequestException("Error: You cannot reject the accepted applicant");
		}
		
		if (applicant.getStatus().compareTo(ApplicantStatus.REJECTED) == 0) {
			throw new BadRequestException("Error: You have rejected this applicant");
		}

		applicant.setStatus(ApplicantStatus.REJECTED);
		applicantRepository.save(applicant);

	}

}
