package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

	private EventRepository eventRepository;
	private UserRepository userRepository;

	@Autowired
	public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Event create(Long userId, CreateEventWrapper wrapper) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: User not found!"));

		if (wrapper.getMaximumAge() > 40 || wrapper.getMinimumAge() < 18) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Age must be between 18 and 40!");
		}

		if (wrapper.getMaximumAge() < wrapper.getMinimumAge()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inputted age error!");
		}

		Event event = new Event();
		event.setUser(user);
		event.setTitle(wrapper.getTitle());
		event.setCity(wrapper.getCity());
		event.setDateAndTime(wrapper.getDateAndTime());
		event.setCompanionGender(wrapper.getCompanionGender());
		event.setMinimumAge(wrapper.getMinimumAge());
		event.setMaximumAge(wrapper.getMaximumAge());
		event.setAdditionalInfo(wrapper.getAdditionalInfo());

		return eventRepository.save(event);

	}

	@Override
	public List<Event> findAll(Integer pageNumber, Integer pageSize, String sortBy, String direction) {
		Pageable paging = Pageable.unpaged();

		if (direction.equalsIgnoreCase("DESC")) {
			paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
		} else if (direction.equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Direction error!");
		}

		Page<Event> pagedResult = eventRepository.findAll(paging);

		if (pagedResult.hasContent()) {
			return pagedResult.getContent();
		} else {
			return new ArrayList<>();
		}
	}
}
