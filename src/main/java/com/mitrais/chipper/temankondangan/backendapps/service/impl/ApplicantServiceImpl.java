package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.mitrais.chipper.temankondangan.backendapps.service.NotificationService;
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
import org.springframework.util.StringUtils;

@Service
public class ApplicantServiceImpl implements ApplicantService {
	private static final String ERROR_EVENT_HAS_FINISHED = "Error: This event has finished already";
	private static final String ERROR_NOT_CREATOR = "Error: Non event creator cannot do this";

	private EventRepository eventRepository;
	private ApplicantRepository applicantRepository;
	private NotificationService notificationService;

	@Autowired
	public ApplicantServiceImpl(EventRepository eventRepository, ApplicantRepository applicantRepository,
								NotificationService notificationService) {
		this.eventRepository = eventRepository;
		this.applicantRepository = applicantRepository;
		this.notificationService = notificationService;
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

		if(Boolean.TRUE.equals(event.getCancelled())) {
			throw new BadRequestException("Error: You cannot accept applicant in cancelled event");
		}

		if (applicant.getStatus() != null && applicant.getStatus().equals(ApplicantStatus.REJECTED)) {
			throw new BadRequestException("Error: You cannot accept rejected applicant");
		}

		if (Boolean.TRUE.equals(applicantRepository.existsByEventAndStatus(event, ApplicantStatus.ACCEPTED))) {
			throw new BadRequestException("Error: You already have accepted applicant");
		}

		applicant.setStatus(ApplicantStatus.ACCEPTED);
		applicantRepository.save(applicant);

		if(!StringUtils.isEmpty(applicant.getApplicantUser().getMessagingToken())) {
			try {
				String title = "Event that you are apply are approved";
				String body = "Event ".concat(event.getTitle()).concat(" are approve you as the applicant");
				Map<String, String> data = new HashMap<>();

				notificationService.send(title, body, applicant.getApplicantUser().getMessagingToken(), data);
			} catch (FirebaseMessagingException e) {

			}
		}
	}

	@Override
	public void cancelAccepted(Long userId, Long applicantId) {
		Map<String, Object> checkResult = checkApplicant(userId, applicantId);
		Applicant applicant = (Applicant) checkResult.get(Entity.APPLICANT.getLabel());
		Event event = (Event) checkResult.get(Entity.EVENT.getLabel());

		if(Boolean.TRUE.equals(event.getCancelled())) {
			throw new BadRequestException("Error: You cannot cancel applicant in cancelled event");
		}

		if (LocalDateTime.now().isAfter(event.getStartDateTime().minusDays(1))) {
			throw new BadRequestException(
					"Error: You cannot cancel the accepted applicant 24 hours before event started");
		}

		if (applicant.getStatus() != null && !applicant.getStatus().equals(ApplicantStatus.ACCEPTED)) {
			throw new BadRequestException("Error: You cannot cancel non accepted applicant");
		}

		applicant.setStatus(ApplicantStatus.APPLIED);
		applicantRepository.save(applicant);

		if(!StringUtils.isEmpty(applicant.getApplicantUser().getMessagingToken())) {
			try{
				String title = "Your accepted application to event are cancelled";
				String body = "Event ".concat(event.getTitle()).concat(" are cancelled you as the applicant");
				Map<String, String> data = new HashMap<>();

				notificationService.send(title, body, applicant.getApplicantUser().getMessagingToken(), data);
			} catch (FirebaseMessagingException e) {

			}
		}
	}
	
	@Override
	public void rejectApplicant(Long userId, Long applicantId) {
		Map<String, Object> checkResult = checkApplicant(userId, applicantId);
		Applicant applicant = (Applicant) checkResult.get(Entity.APPLICANT.getLabel());

		if(Boolean.TRUE.equals(applicant.getEvent().getCancelled())) {
			throw new BadRequestException("Error: You cannot reject applicant in cancelled event");
		}
		
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
