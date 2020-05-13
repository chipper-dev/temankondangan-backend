package com.mitrais.chipper.temankondangan.backendapps.service;

import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;

public interface EventService {

	Event create(Long userId, CreateEventWrapper wrapper);

	List<Event> findAll(Integer pageNumber, Integer pageSize, String sortBy, String direction);

	public Event edit(Long userId, EditEventWrapper wrapper);

//	public List<Event> apply(Long userId, Long eventId);

	EventDetailResponseWrapper findById(Long id);

}
