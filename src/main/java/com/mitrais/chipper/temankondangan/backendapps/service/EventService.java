package com.mitrais.chipper.temankondangan.backendapps.service;

import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;

public interface EventService {

	public Event create(Long userId, CreateEventWrapper wrapper);

	public List<Event> findAll(Integer pageNumber, Integer pageSize, String sortBy, String direction);

}
