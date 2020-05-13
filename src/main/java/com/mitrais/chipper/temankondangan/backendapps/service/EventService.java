package com.mitrais.chipper.temankondangan.backendapps.service;

import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;

public interface EventService {

	public Event create(Long userId, CreateEventWrapper wrapper);

	public List<Event> findAll(Integer pageNumber, Integer pageSize, String sortBy, String direction, Long userId);

	public Event edit(Long userId, EditEventWrapper wrapper);

	public EventDetailResponseWrapper findById(Long id);

	public void apply(Long userId, Long eventId);

}
