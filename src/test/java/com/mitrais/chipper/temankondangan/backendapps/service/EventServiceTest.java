package com.mitrais.chipper.temankondangan.backendapps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

	@BeforeAll
	public void init() {

		User user = new User(1L, "test@email.com", "12345_", null, null, null, DataState.ACTIVE);
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
	}

	@Test
	public void UpdateProfileTestWithImage() throws IOException {
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

}
