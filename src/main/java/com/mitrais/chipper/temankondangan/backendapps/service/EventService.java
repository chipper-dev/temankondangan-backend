package com.mitrais.chipper.temankondangan.backendapps.service;

import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.json.AppliedEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.SearchEventWrapper;

public interface EventService {

	public Event create(Long userId, CreateEventWrapper wrapper);

	public EventFindAllResponseWrapper findAll(Integer pageNumber, Integer pageSize, String sortBy, String direction,
			Long userId);

	public List<EventFindAllListDBResponseWrapper> findMyEvent(String sortBy, String direction, Long userId,
			boolean current);

	public Event edit(Long userId, EditEventWrapper wrapper);

	public void apply(Long userId, Long eventId);

	public EventDetailResponseWrapper findEventDetail(String id, Long userId);

	public void cancelEvent(Long userId, Long eventId);

	public List<AppliedEventWrapper> findActiveAppliedEvent(Long userId, String sortBy, String direction);

	public List<AppliedEventWrapper> findPastAppliedEvent(Long userId, String sortBy, String direction);

	public void creatorCancelEvent(Long userId, Long eventId);


	public EventFindAllResponseWrapper search(Long userId, SearchEventWrapper wrapper);
}
