package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.EventService;

@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Event create(Long userId, CreateEventWrapper wrapper) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: User not found!"));

		Event event = new Event();
		event.setUser(user);
		event.setTitle(wrapper.getTitle());
		event.setDateAndTime(wrapper.getDateAndTime());
		event.setCompanionGender(wrapper.getCompanionGender());
		event.setMinimumAge(wrapper.getMinimumAge());
		event.setMaximumAge(wrapper.getMaximumAge());
		event.setAdditionalInfo(wrapper.getAdditionalInfo());

		return eventRepository.save(event);

	}
}
