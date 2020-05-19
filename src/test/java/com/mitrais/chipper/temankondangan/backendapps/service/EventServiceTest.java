package com.mitrais.chipper.temankondangan.backendapps.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.EventServiceImpl;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	EventRepository eventRepository;

	@Mock
	ProfileRepository profileRepository;

	@Mock
	ApplicantRepository applicantRepository;

	@InjectMocks
	EventServiceImpl eventService;

	private static CreateEventWrapper wrapper;
	private static Event event;
	private static User user;
	private static Page<EventFindAllListDBResponseWrapper> pageEvent;
	private static List<EventFindAllListDBResponseWrapper> eventList;

	@BeforeEach
	public void init() {

		wrapper = new CreateEventWrapper();
		wrapper.setAdditionalInfo("info test");
		wrapper.setCompanionGender(Gender.P);
		wrapper.setStartDateTime(
				LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		wrapper.setFinishDateTime(
				LocalDateTime.now().plusDays(4).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		wrapper.setMaximumAge(25);
		wrapper.setMinimumAge(18);
		wrapper.setTitle("title test");
		wrapper.setCity("Test City");

		user = new User(1L, "test@email.com", "12345_", null, null, null, DataState.ACTIVE);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(userOptional);

	}

	// create event service
	@Test
	public void createEventTest() {
		event = new Event();
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now());
		event.setFinishDateTime(LocalDateTime.now().plusHours(4));
		event.setMaximumAge(25);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenReturn(event);
		Event result = eventService.create(1L, wrapper);
		assertEquals(event.getTitle(), result.getTitle());
	}

	@Test
	public void shouldThrowBadRequestException_WhenAgeLessThan18() {
		wrapper.setMinimumAge(17);
		assertThatThrownBy(() -> eventService.create(1L, wrapper))
				.hasMessageContaining("Error: Minimum age must be 18!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenMinimumAgeIsMoreThanMaximumAge() {
		wrapper.setMinimumAge(25);
		wrapper.setMaximumAge(20);
		assertThatThrownBy(() -> eventService.create(1L, wrapper))
				.hasMessageContaining("Error: Inputted age is not valid!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowDateTimeParseException_WhenDateFormatIsNotValid() {
		wrapper.setStartDateTime(
				LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss")));
		assertThatThrownBy(() -> eventService.create(1L, wrapper)).isInstanceOf(DateTimeParseException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenStartDateIsBeforeTodayPlus1() {
		wrapper.setStartDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		assertThatThrownBy(() -> eventService.create(1L, wrapper))
				.hasMessageContaining("Error: Date inputted have to be after today!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenStartDateIsMoreThanFinishDate() {
		wrapper.setStartDateTime(
				LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		wrapper.setFinishDateTime(
				LocalDateTime.now().plusDays(3).minusHours(2).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));

		assertThatThrownBy(() -> eventService.create(1L, wrapper))
				.hasMessageContaining("Error: Start time must be earlier than finish time!")
				.isInstanceOf(BadRequestException.class);
	}

	// find all service
	@Test
	public void findAllEventTest() {

		EventFindAllListDBResponseWrapper event2 = new EventFindAllListDBResponseWrapper();
		event2.setCompanionGender(Gender.P);
		event2.setStartDateTime(LocalDateTime.now());
		event2.setFinishDateTime(LocalDateTime.now().plusHours(4));
		event2.setMaximumAge(25);
		event2.setMinimumAge(18);
		event2.setTitle("title test 2");
		event2.setCity("Test City");
		
		EventFindAllListDBResponseWrapper event3 = new EventFindAllListDBResponseWrapper();
		event3.setCompanionGender(Gender.P);
		event3.setStartDateTime(LocalDateTime.now());
		event3.setFinishDateTime(LocalDateTime.now().plusHours(4));
		event3.setMaximumAge(25);
		event3.setMinimumAge(18);
		event3.setTitle("title test 3");
		event3.setCity("Test City");
		
		eventList = new ArrayList<>();
		eventList.add(event2);
		eventList.add(event3);

		pageEvent = new PageImpl<EventFindAllListDBResponseWrapper>(eventList);
		
		Profile profile1 = new Profile();
		profile1.setGender(Gender.P);
		profile1.setDob(LocalDate.now().minusYears(19));

		Optional<Profile> profileOptional = Optional.of(profile1);
		Mockito.when(profileRepository.findByUserId(Mockito.any(Long.class))).thenReturn(profileOptional);
		Mockito.when(eventRepository.findAllByRelevantInfo(Mockito.any(Integer.class), Mockito.anyCollection(),
				Mockito.any(Long.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class))).thenReturn(pageEvent);

		EventFindAllResponseWrapper events = eventService.findAll(0, 1, "test sort key", "DESC", 1L);
		assertEquals("title test 2", events.getContentList().get(0).getTitle());
	}

	@Test
	public void findEventDetailForCreatorTest() {
		User userApplicant = new User(2L, "test@email.com", "12345_", null, null, null, DataState.ACTIVE);

		Profile profileCreator = Profile.builder().user(user).profileId(1L).fullName("john doe").build();

		Profile profileApplicant = Profile.builder().profileId(2L).user(userApplicant).fullName("jane doe").build();

		Applicant applicant = Applicant.builder().applicantUser(userApplicant).event(event).dataState(DataState.ACTIVE)
				.status(ApplicantStatus.APPLIED).build();

		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
		Mockito.when(applicantRepository.findByEventId(event.getEventId())).thenReturn(Arrays.asList(applicant));

		Mockito.when(profileRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(profileCreator));
		Mockito.when(profileRepository.findByUserId(userApplicant.getUserId()))
				.thenReturn(Optional.of(profileApplicant));

		EventDetailResponseWrapper actualResult = eventService.findEventDetail("1", 1L);

		assertEquals("title test", actualResult.getTitle());
		assertEquals("", actualResult.getPhotoProfileUrl());
		assertFalse(actualResult.getApplicantList().isEmpty());
		assertTrue(actualResult.getIsCreator());
		assertEquals("jane doe", actualResult.getApplicantList().get(0).getFullName());
	}
}
