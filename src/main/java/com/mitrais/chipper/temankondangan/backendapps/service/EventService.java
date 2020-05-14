package com.mitrais.chipper.temankondangan.backendapps.service;

import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;

public interface EventService {

	Event create(Long userId, CreateEventWrapper wrapper);

	List<Event> findAll(Integer pageNumber, Integer pageSize, String sortBy, String direction, Long userId);

	Event edit(Long userId, EditEventWrapper wrapper);

	EventDetailResponseWrapper findEventDetail(Long id, Long userId);

	void cancelEvent(Long userId, Long eventId);
}
