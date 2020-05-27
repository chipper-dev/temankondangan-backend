package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllResponseWrapper;

public interface EventService {

	Event create(Long userId, CreateEventWrapper wrapper);

	public EventFindAllResponseWrapper findAll(Integer pageNumber, Integer pageSize, String sortBy, String direction,
			Long userId);

	Event edit(Long userId, EditEventWrapper wrapper);

	void apply(Long userId, Long eventId);

	EventDetailResponseWrapper findEventDetail(String id, Long userId);

	void cancelEvent(Long userId, Long eventId);

}
