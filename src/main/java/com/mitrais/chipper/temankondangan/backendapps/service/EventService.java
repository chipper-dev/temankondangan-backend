package com.mitrais.chipper.temankondangan.backendapps.service;

import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.json.AppliedEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllResponseWrapper;

public interface EventService {

	public Event create(Long userId, CreateEventWrapper wrapper);

	public EventFindAllResponseWrapper findAll(String header, Integer pageNumber, Integer pageSize, String sortBy,
			String direction, Long userId);

	public EventDetailResponseWrapper findEventDetail(String header, String eventId, Long userId);

	public List<EventFindAllListDBResponseWrapper> findMyEvent(String header, String sortBy, String direction, Long userId,
			boolean current);

	public Event edit(String header, Long userId, EditEventWrapper wrapper);

	public void apply(String header, Long userId, Long eventId);

	public void cancelEvent(String header, Long userApplicantId, Long eventId);

	public void creatorCancelEvent(String header, Long userId, Long eventId);

	public List<AppliedEventWrapper> findActiveAppliedEvent(String header, Long userId, String sortBy, String direction,
			String applicantStatusStr);

	public List<AppliedEventWrapper> findPastAppliedEvent(String header, Long userId, String sortBy, String direction,
			String applicantStatusStr);

	public EventFindAllResponseWrapper search(String header, Long userId, Integer pageNumber, Integer pageSize, String sortBy,
			String direction, String creatorGender, Integer creatorMaximumAge, Integer creatorMinimumAge,
			String startDate, String finishDate, List<String> startHour, List<String> finishHour, List<String> city,
			Double zoneOffset);

}
