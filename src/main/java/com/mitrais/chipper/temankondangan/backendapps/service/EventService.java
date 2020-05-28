package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.json.*;

import java.util.List;

public interface EventService {

	Event create(Long userId, CreateEventWrapper wrapper);

	public EventFindAllResponseWrapper findAll(Integer pageNumber, Integer pageSize, String sortBy,
			String direction, Long userId);

	Event edit(Long userId, EditEventWrapper wrapper);

	void apply(Long userId, Long eventId);

	EventDetailResponseWrapper findEventDetail(String id, Long userId);

	void cancelEvent(Long userId, Long eventId);

	List<AppliedEventWrapper> findActiveAppliedEvent(Long userId);
}
