package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.microservice.MicroserviceToLegacy;
import com.mitrais.chipper.temankondangan.backendapps.microservice.dto.ProfileMicroservicesDTO;
import com.mitrais.chipper.temankondangan.backendapps.microservice.feign.ProfileFeignClient;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Entity;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ApplicantService;

@Service
public class ApplicantServiceImpl implements ApplicantService {
	private static final Logger logger = LoggerFactory.getLogger(ApplicantServiceImpl.class);
	private static final String ERROR_EVENT_HAS_FINISHED = "Error: This event has finished already";
	private static final String ERROR_NOT_CREATOR = "Error: Non event creator cannot do this";
	private static final String DEFAULT_NO_NAME = "Someone";

	enum NotificationType {ACCEPT_APPLICANT, REJECT_APPLICANT, CANCEL_APPLICANT}

	private EventRepository eventRepository;
	private ApplicantRepository applicantRepository;
	private NotificationService notificationService;
	private ProfileFeignClient profileFeignClient;

	@Autowired
	public ApplicantServiceImpl(EventRepository eventRepository, ApplicantRepository applicantRepository,
								NotificationService notificationService, ProfileFeignClient profileFeignClient, MicroserviceToLegacy msConverter) {
		this.eventRepository = eventRepository;
		this.applicantRepository = applicantRepository;
		this.notificationService = notificationService;
		this.profileFeignClient = profileFeignClient;
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
	public void accept(String header, Long userId, Long applicantId) {
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

		sendNotification(header, applicant.getEvent().getUser().getUserId(), NotificationType.ACCEPT_APPLICANT, applicant.getEvent().getTitle(), applicant.getApplicantUser(), applicant.getEvent().getEventId());

	}

	@Override
	public void cancelAccepted(String header, Long userId, Long applicantId) {
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

		sendNotification(header, applicant.getEvent().getUser().getUserId(), NotificationType.CANCEL_APPLICANT, applicant.getEvent().getTitle(), applicant.getApplicantUser(), applicant.getEvent().getEventId());

	}
	
	@Override
	public void rejectApplicant(String header, Long userId, Long applicantId) {
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

		sendNotification(header, applicant.getEvent().getUser().getUserId(), NotificationType.REJECT_APPLICANT, applicant.getEvent().getTitle(), applicant.getApplicantUser(), applicant.getEvent().getEventId());

	}

	private void sendNotification(String header, Long userId, NotificationType type, String eventTitle, User userDestination, Long eventId) {
		ProfileMicroservicesDTO profile = profileFeignClient.findByUserId(header, userId).orElse(null);
		String name = profile == null ? DEFAULT_NO_NAME : profile.getFullName();
		String title = titleNotificationMsg(type);
		String body =  bodyNotificationMsg(type, name, eventTitle);
		Map<String, String> data = new HashMap<>();
		data.put("eventId", eventId.toString());
		data.put("isMyEvent", Boolean.FALSE.toString());

		try {
			notificationService.send(title, body, userDestination, data);
		} catch (FirebaseMessagingException e) {
			logger.error("FirebaseMessagingException", e);
		}
	}

	private String titleNotificationMsg(NotificationType notificationType) {
		switch (notificationType) {
			case ACCEPT_APPLICANT:
				return "Your event application was accepted";
			case CANCEL_APPLICANT:
				return "Your event application was cancelled";
			case REJECT_APPLICANT:
				return "Your event application was rejected";
			default:
				return "";
		}
	}

	private String bodyNotificationMsg(NotificationType notificationType, String name, String titleEvent) {
		switch (notificationType) {
			case ACCEPT_APPLICANT:
				return name + " accept your application to " + titleEvent;
			case CANCEL_APPLICANT:
				return name + " cancelling acceptance of your application to " + titleEvent;
			case REJECT_APPLICANT:
				return name + " reject your application to " + titleEvent;
			default:
				return "";
		}
	}

}
