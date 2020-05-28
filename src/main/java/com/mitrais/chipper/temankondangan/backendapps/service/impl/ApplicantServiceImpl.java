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

		if (LocalDateTime.now().isAfter(event.getFinishDateTime())) {
			throw new BadRequestException("Error: This event has finished already");
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

		if (LocalDateTime.now().isAfter(event.getStartDateTime().minusDays(1))) {
			throw new BadRequestException("Error: You cannot cancel accepted applicant 24 hours before event started");
		}

		if (applicant.getStatus() != null && !applicant.getStatus().equals(ApplicantStatus.ACCEPTED)) {
			throw new BadRequestException("Error: You cannot cancel non accepted applicant");
		}

		applicant.setStatus(ApplicantStatus.APPLIED);
		applicantRepository.save(applicant);

	}

}
