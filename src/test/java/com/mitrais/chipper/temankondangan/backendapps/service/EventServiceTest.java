package com.mitrais.chipper.temankondangan.backendapps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.EventServiceImpl;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventServiceTest {

	@Autowired
	EventServiceImpl eventService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	EventRepository eventRepository;

	private static Event event;
	private static User user;
	private static Page<Event> pageEvent;

	@BeforeAll
	public void init() {

		user = new User(1L, "test@email.com", "12345_", null, null, null, DataState.ACTIVE);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);

		event = new Event();
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setDateAndTime(LocalDateTime.now());
		event.setMaximumAge(25);
		event.setMinimumAge(18);
		event.setTitle("title test");

		Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenReturn(event);

		Event event2 = new Event();
		event2 = new Event();
		event2.setUser(user);
		event2.setAdditionalInfo("info test 2");
		event2.setCompanionGender(Gender.P);
		event2.setDateAndTime(LocalDateTime.now());
		event2.setMaximumAge(25);
		event2.setMinimumAge(18);
		event2.setTitle("title test 2");

		Event event3 = new Event();
		event3 = new Event();
		event3.setUser(user);
		event3.setAdditionalInfo("info test 3");
		event3.setCompanionGender(Gender.P);
		event3.setDateAndTime(LocalDateTime.now());
		event3.setMaximumAge(25);
		event3.setMinimumAge(18);
		event3.setTitle("title test 3");

		List<Event> eventList = new ArrayList<>();
		eventList.add(event);
		eventList.add(event2);
		eventList.add(event3);

		pageEvent = new PageImpl<Event>(eventList);
		pageEvent.getSort();
	}

	@Test
	public void createEventTest() {
		CreateEventWrapper wrapper = new CreateEventWrapper();
		wrapper.setAdditionalInfo("info test");
		wrapper.setCompanionGender(Gender.P);
		wrapper.setDateAndTime(LocalDateTime.now());
		wrapper.setMaximumAge(25);
		wrapper.setMinimumAge(18);
		wrapper.setTitle("title test");

		Event result = eventService.create(1L, wrapper);
		assertEquals(event.getTitle(), result.getTitle());
	}

	@Test
	public void findAllEventTest_Descending() {
		Mockito.when(eventRepository.findAll(Mockito.any(Pageable.class))).thenReturn(pageEvent);

		List<Event> events = eventService.findAll(1, 1, "test sort key", "DESC");
		assertEquals("title test", events.get(0).getTitle());
	}

	@Test
	public void findAllEventTest_Ascending() {
		pageEvent.getSort().ascending();
		Mockito.when(eventRepository.findAll(Mockito.any(Pageable.class))).thenReturn(pageEvent);

		List<Event> events = eventService.findAll(1, 1, "test sort key", "ASC");
		assertEquals("title test", events.get(0).getTitle());
	}
}
