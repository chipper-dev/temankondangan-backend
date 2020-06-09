package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.*;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.EventServiceImpl;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.ImageFileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

	@Mock
	ImageFileServiceImpl imageFileService;

	@InjectMocks
	EventServiceImpl eventService;

	private static CreateEventWrapper wrapper;
	private static Event event;
	private static User user;
	private static Profile profile;
	private static Page<EventFindAllListDBResponseWrapper> pageEvent;
	private static List<EventFindAllListDBResponseWrapper> eventList;
		
	@BeforeEach
	public void init() {

		user = new User(1L, "test@email.com", "12345_", null, null, null, DataState.ACTIVE);
		Optional<User> userOptional = Optional.of(user);
		Mockito.when(userRepository.findById(anyLong())).thenReturn(userOptional);

	}

	// create event service
	@Test
	public void createEventTest() {

		wrapper = new CreateEventWrapper();
		wrapper.setAdditionalInfo("info test");
		wrapper.setCompanionGender(Gender.P);
		wrapper.setStartDateTime(
				LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		wrapper.setFinishDateTime(
				LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		wrapper.setMaximumAge(25);
		wrapper.setMinimumAge(18);
		wrapper.setTitle("title test");
		wrapper.setCity("Test City");

		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));
		Event result = eventService.create(1L, wrapper);
		assertEquals("title test", result.getTitle());
	}

	@Test
	public void createEventTestWhenAge40() {

		wrapper = new CreateEventWrapper();
		wrapper.setAdditionalInfo("info test");
		wrapper.setCompanionGender(Gender.P);
		wrapper.setStartDateTime(
				LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		wrapper.setFinishDateTime(
				LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		wrapper.setMaximumAge(40);
		wrapper.setMinimumAge(18);
		wrapper.setTitle("title test");
		wrapper.setCity("Test City");

		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));
		Event result = eventService.create(1L, wrapper);
		assertEquals(150, result.getMaximumAge());
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenUserNotFoundInCreateEvent() {
		wrapper = new CreateEventWrapper();
		Mockito.when(userRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> eventService.create(1L, wrapper)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenAgeLessThan18() {
		wrapper = new CreateEventWrapper();
		wrapper.setMinimumAge(17);
		assertThatThrownBy(() -> eventService.create(1L, wrapper))
				.hasMessageContaining("Error: Minimum age must be 18!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenMinimumAgeIsMoreThanMaximumAgeInCreateEvent() {
		wrapper = new CreateEventWrapper();
		wrapper.setMinimumAge(25);
		wrapper.setMaximumAge(20);
		assertThatThrownBy(() -> eventService.create(1L, wrapper))
				.hasMessageContaining("Error: Inputted age is not valid!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowDateTimeParseException_WhenDateFormatIsNotValidInCreateEvent() {
		wrapper = new CreateEventWrapper();
		wrapper.setMaximumAge(40);
		wrapper.setMinimumAge(18);

		wrapper.setStartDateTime(
				LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss")));
		assertThatThrownBy(() -> eventService.create(1L, wrapper)).isInstanceOf(DateTimeParseException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenStartDateIsBeforeTodayPlus1InCreateEvent() {
		wrapper = new CreateEventWrapper();
		wrapper.setMaximumAge(40);
		wrapper.setMinimumAge(18);

		wrapper.setStartDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		assertThatThrownBy(() -> eventService.create(1L, wrapper))
				.hasMessageContaining("Error: Date inputted have to be after today!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenStartDateIsMoreThanFinishDateInCreateEvent() {
		wrapper = new CreateEventWrapper();
		wrapper.setMaximumAge(40);
		wrapper.setMinimumAge(18);
		wrapper.setStartDateTime(
				LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		wrapper.setFinishDateTime(
				LocalDateTime.now().plusDays(3).minusHours(1).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));

		assertThatThrownBy(() -> eventService.create(1L, wrapper))
				.hasMessageContaining("Error: Start time must be earlier than finish time!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenStartDateIsNotTheSameWithFinishDateInCreateEvent() {
		wrapper = new CreateEventWrapper();
		wrapper.setMaximumAge(40);
		wrapper.setMinimumAge(18);
		wrapper.setStartDateTime(
				LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));
		wrapper.setFinishDateTime(
				LocalDateTime.now().plusDays(4).format(DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")));

		assertThatThrownBy(() -> eventService.create(1L, wrapper))
				.hasMessageContaining("Error: Start date and finish date must be the same day!")
				.isInstanceOf(BadRequestException.class);
	}

	// find all service
	@Test
	public void findAllEventTestDesc() {

		EventFindAllListDBResponseWrapper event2 = new EventFindAllListDBResponseWrapper();
		event2.setCompanionGender(Gender.P);
		event2.setStartDateTime(LocalDateTime.now());
		event2.setFinishDateTime(LocalDateTime.now().plusHours(1));
		event2.setMaximumAge(25);
		event2.setMinimumAge(18);
		event2.setTitle("title test 2");
		event2.setCity("Test City");

		EventFindAllListDBResponseWrapper event3 = new EventFindAllListDBResponseWrapper();
		event3.setCompanionGender(Gender.P);
		event3.setStartDateTime(LocalDateTime.now());
		event3.setFinishDateTime(LocalDateTime.now().plusHours(1));
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
		Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(profileOptional);
		Mockito.when(imageFileService.getImageUrl(profile1)).thenReturn("");
		Mockito.when(eventRepository.findAllByRelevantInfo(any(Integer.class), Mockito.anyCollection(),
				anyLong(), any(LocalDateTime.class), any(Pageable.class)))
				.thenReturn(pageEvent);

		EventFindAllResponseWrapper events = eventService.findAll(0, 1, "createdDate", "DESC", 1L);
		assertEquals("title test 2", events.getContentList().get(0).getTitle());
	}

	@Test
	public void findAllEventTestAsc() {

		EventFindAllListDBResponseWrapper event2 = new EventFindAllListDBResponseWrapper();
		event2.setCompanionGender(Gender.P);
		event2.setStartDateTime(LocalDateTime.now());
		event2.setFinishDateTime(LocalDateTime.now().plusHours(1));
		event2.setMaximumAge(25);
		event2.setMinimumAge(18);
		event2.setTitle("title test 2");
		event2.setCity("Test City");

		EventFindAllListDBResponseWrapper event3 = new EventFindAllListDBResponseWrapper();
		event3.setCompanionGender(Gender.P);
		event3.setStartDateTime(LocalDateTime.now());
		event3.setFinishDateTime(LocalDateTime.now().plusHours(1));
		event3.setMaximumAge(25);
		event3.setMinimumAge(18);
		event3.setTitle("title test 3");
		event3.setCity("Test City");

		eventList = new ArrayList<>();
		eventList.add(event3);
		eventList.add(event2);

		pageEvent = new PageImpl<EventFindAllListDBResponseWrapper>(eventList);

		Profile profile1 = new Profile();
		profile1.setGender(Gender.P);
		profile1.setDob(LocalDate.now().minusYears(19));

		Optional<Profile> profileOptional = Optional.of(profile1);
		Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(profileOptional);
		Mockito.when(imageFileService.getImageUrl(profile1)).thenReturn("");
		Mockito.when(eventRepository.findAllByRelevantInfo(any(Integer.class), Mockito.anyCollection(),
				anyLong(), any(LocalDateTime.class), any(Pageable.class)))
				.thenReturn(pageEvent);

		EventFindAllResponseWrapper events = eventService.findAll(0, 1, "createdDate", "ASC", 1L);
		assertEquals("title test 3", events.getContentList().get(0).getTitle());
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenProfileNotFoundInFindAllEvent() {

		Mockito.when(profileRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> eventService.findAll(0, 1, "createdDate", "DESC", 1L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenSortByParamNotFilledWithCorrectValue() {
		Profile profile1 = new Profile();
		profile1.setGender(Gender.P);
		profile1.setDob(LocalDate.now().minusYears(19));

		Optional<Profile> profileOptional = Optional.of(profile1);
		Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(profileOptional);
		
		assertThatThrownBy(() -> eventService.findAll(0, 1, "wrong sort key", "DESC", 1L))
		.hasMessageContaining("Error: Can only input createdDate or startDateTime for sortBy!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenDirectionParamNotFilledWithCorrectValue() {
		Profile profile1 = new Profile();
		profile1.setGender(Gender.P);
		profile1.setDob(LocalDate.now().minusYears(19));

		Optional<Profile> profileOptional = Optional.of(profile1);
		Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(profileOptional);
		
		assertThatThrownBy(() -> eventService.findAll(0, 1, "createdDate", "wrong direction", 1L))
		.hasMessageContaining("Error: Can only input ASC or DESC for direction!").isInstanceOf(BadRequestException.class);
	}
	
	// find Event Detail
	@Test
	public void findEventDetailForCreatorTest() {
		User userApplicant = new User(2L, "test@email.com", "12345_", null, null, null, DataState.ACTIVE);

		Profile profileCreator = Profile.builder().user(user).profileId(1L).fullName("john doe").build();

		Profile profileApplicant = Profile.builder().profileId(2L).user(userApplicant).fullName("jane doe").build();

		Applicant applicant = Applicant.builder().applicantUser(userApplicant).event(event).dataState(DataState.ACTIVE)
				.status(ApplicantStatus.APPLIED).build();

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		Mockito.when(applicantRepository.findByEventId(event.getEventId())).thenReturn(Optional.of(Arrays.asList(applicant)));

		Mockito.when(profileRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(profileCreator));
		Mockito.when(profileRepository.findByUserId(userApplicant.getUserId()))
				.thenReturn(Optional.of(profileApplicant));

		Mockito.when(imageFileService.getImageUrl(profileCreator)).thenReturn(Mockito.anyString());

		EventDetailResponseWrapper actualResult = eventService.findEventDetail("1", 1L);

		assertEquals("title test", actualResult.getTitle());
		assertEquals("", actualResult.getPhotoProfileUrl());
		assertFalse(actualResult.getApplicantList().isEmpty());
		assertTrue(actualResult.getIsCreator());
		assertEquals("jane doe", actualResult.getApplicantList().get(0).getFullName());
	}

	// apply service
	@Test
	public void applyEventTest() {
		User user2 = new User();
		user2.setUserId(2L);
		event.setUser(user2);
		event.setFinishDateTime(LocalDateTime.now().plusHours(2));
		event.setStartDateTime(LocalDateTime.now().plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setCompanionGender(Gender.B);
		profile = new Profile();
		profile.setDob(LocalDate.of(1995, 1, 1));
		profile.setGender(Gender.L);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(
				applicantRepository.existsByApplicantUserAndEvent(any(User.class), any(Event.class)))
				.thenReturn(false);
		Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(profile));


		Answer<Applicant> answer = new Answer<Applicant>() {
			public Applicant answer(InvocationOnMock invocation) throws Throwable {
				user2.setUserId(3L);
				Applicant applicant = invocation.getArgument(0, Applicant.class);
				applicant.setId(1L);
				applicant.setApplicantUser(user2);
				applicant.setEvent(event);

				return applicant;
			}
		};

		doAnswer(answer).when(applicantRepository).save(any(Applicant.class));
		eventService.apply(1L, 1L);
		assertEquals(3L, user2.getUserId());
	}

	@Test
	public void shouldThrowBadRequestException_WhenUserAgeDoesNotMeetApplyAgeRequirement() {
		User user2 = new User();
		user2.setUserId(2L);
		event.setUser(user2);
		event.setFinishDateTime(LocalDateTime.now().plusDays(1));
		event.setMaximumAge(20);
		event.setMinimumAge(18);
		profile = new Profile();
		profile.setDob(LocalDate.of(1995, 1, 1));

		Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(profile));
		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(
				applicantRepository.existsByApplicantUserAndEvent(any(User.class), any(Event.class)))
				.thenReturn(false);

		assertThatThrownBy(() -> eventService.apply(1L, 1L))
				.hasMessageContaining("Error: Your age does not meet the requirement").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenUserGenderDoesNotMeetApplyGenderRequirement() {
		User user2 = new User();
		user2.setUserId(2L);
		event.setUser(user2);
		event.setFinishDateTime(LocalDateTime.now().plusDays(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setCompanionGender(Gender.L);
		profile = new Profile();
		profile.setDob(LocalDate.of(1995, 1, 1));
		profile.setGender(Gender.P);

		Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(profile));
		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(
				applicantRepository.existsByApplicantUserAndEvent(any(User.class), any(Event.class)))
				.thenReturn(false);

		assertThatThrownBy(() -> eventService.apply(1L, 1L))
				.hasMessageContaining("Error: Your gender does not meet the requirement").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenUserApplyAfterEventHasFinishedAlready() {
		User user2 = new User();
		user2.setUserId(2L);
		event.setUser(user2);
		event.setFinishDateTime(LocalDateTime.now().minusHours(1));
		profile = new Profile();
		profile.setDob(LocalDate.of(1995, 1, 1));

		Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(profile));
		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(
				applicantRepository.existsByApplicantUserAndEvent(any(User.class), any(Event.class)))
				.thenReturn(false);

		assertThatThrownBy(() -> eventService.apply(1L, 1L))
				.hasMessageContaining("Error: This event has finished already").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenCreatorUserIdEqualToApplicantUserId() {
		event = new Event();
		event.setUser(user);
		profile = new Profile();
		profile.setDob(LocalDate.of(1995, 1, 1));

		Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(profile));
		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		assertThatThrownBy(() -> eventService.apply(1L, 1L))
				.hasMessageContaining("Error: You cannot apply to your own event!")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenApplicantHasAppliedToEvent() {
		User user2 = new User();
		user2.setUserId(2L);
		event.setUser(user2);
		profile = new Profile();
		profile.setDob(LocalDate.of(1995, 1, 1));

		Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(profile));
		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(
				applicantRepository.existsByApplicantUserAndEvent(any(User.class), any(Event.class)))
				.thenReturn(true);
		assertThatThrownBy(() -> eventService.apply(1L, 1L))
				.hasMessageContaining("Error: You have applied to this event").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenUserNotFoundInApplyEvent() {
		Mockito.when(userRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> eventService.apply(1L, 1L)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenEventNotFoundInApplyEvent() {
		Mockito.when(eventRepository.findById(anyLong())).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> eventService.apply(1L, 1L)).isInstanceOf(ResourceNotFoundException.class);
	}

    @Test
    public void findActiveAppliedEventTest() {
        event = new Event();
        event.setEventId(1L);
        event.setUser(user);
        event.setAdditionalInfo("info test");
        event.setCompanionGender(Gender.P);
        event.setStartDateTime(LocalDateTime.now().plusDays(1));
        event.setFinishDateTime(LocalDateTime.now().plusDays(1).plusHours(1));
        event.setMaximumAge(40);
        event.setMinimumAge(18);
        event.setTitle("title test");
        event.setCity("Test City");
        event.setDataState(DataState.ACTIVE);

        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        User user2 = new User();
        user2.setUserId(2L);

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setProfileId(1L);
        profile.setFullName("John Doe");
        profile.setPhotoProfileFilename("image.jpg");

        Applicant applicant = new Applicant();
        applicant.setId(1L);
        applicant.setApplicantUser(user2);
        applicant.setEvent(event);
        applicant.setStatus(ApplicantStatus.APPLIED);

        Mockito.when(imageFileService.getImageUrl(any(Profile.class))).thenReturn(profile.getPhotoProfileFilename());
        Mockito.when(eventRepository.findAppliedEvent(anyLong(), any(DataState.class), any(LocalDateTime.class), anyInt(), any(Sort.class))).thenReturn(eventList);
        Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(profile));
        Mockito.when(applicantRepository.findByApplicantUserIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.of(applicant));

        List<AppliedEventWrapper> resultList = eventService.findActiveAppliedEvent(2L, "createdDate", "DESC");

        assertFalse(resultList.isEmpty());
		assertEquals( "image.jpg", resultList.get(0).getPhotoProfileUrl());
		assertEquals("title test", resultList.get(0).getTitle());
    }

    @Test
    public void findPastAppliedEventTest() {
        event = new Event();
        event.setEventId(1L);
        event.setUser(user);
        event.setAdditionalInfo("info test");
        event.setCompanionGender(Gender.P);
        event.setStartDateTime(LocalDateTime.now().minusDays(1));
        event.setFinishDateTime(LocalDateTime.now().minusDays(1).plusHours(1));
        event.setMaximumAge(40);
        event.setMinimumAge(18);
        event.setTitle("title test");
        event.setCity("Test City");
        event.setDataState(DataState.ACTIVE);

        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        User user2 = new User();
        user2.setUserId(2L);

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setProfileId(1L);
        profile.setFullName("John Doe");
        profile.setPhotoProfileFilename("image.jpg");

        Applicant applicant = new Applicant();
        applicant.setId(1L);
        applicant.setApplicantUser(user2);
        applicant.setEvent(event);
        applicant.setStatus(ApplicantStatus.APPLIED);

        Mockito.when(imageFileService.getImageUrl(any(Profile.class))).thenReturn(profile.getPhotoProfileFilename());
        Mockito.when(eventRepository.findAppliedEvent(anyLong(), any(DataState.class), any(LocalDateTime.class), anyInt(), any(Sort.class))).thenReturn(eventList);
        Mockito.when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(profile));
        Mockito.when(applicantRepository.findByApplicantUserIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.of(applicant));

        List<AppliedEventWrapper> resultList = eventService.findPastAppliedEvent(2L, "createdDate", "DESC");

        assertFalse(resultList.isEmpty());
        assertEquals("image.jpg", resultList.get(0).getPhotoProfileUrl());
        assertEquals("title test", resultList.get(0).getTitle());
    }

    @Test
	public void editEventSuccess() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusDays(3));
		event.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		EditEventWrapper editEvent = new EditEventWrapper();
		editEvent.setEventId(1L);
		editEvent.setTitle("title test edited");
		editEvent.setCity("Test City");
		editEvent.setAdditionalInfo("info test");
		editEvent.setCompanionGender(Gender.P);
		editEvent.setStartDateTime(LocalDateTime.now().plusDays(3).format(formatter));
		editEvent.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1).format(formatter));
		editEvent.setMaximumAge(20);
		editEvent.setMinimumAge(18);
		Event eventEdited = eventService.edit(1L, editEvent);

		assertEquals("title test edited", eventEdited.getTitle());
	}

	@Test
	public void editEventSuccessWhenMaxAge40() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusDays(3));
		event.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		EditEventWrapper editEvent = new EditEventWrapper();
		editEvent.setEventId(1L);
		editEvent.setTitle("title test edited");
		editEvent.setCity("Test City");
		editEvent.setAdditionalInfo("info test");
		editEvent.setCompanionGender(Gender.P);
		editEvent.setStartDateTime(LocalDateTime.now().plusDays(3).format(formatter));
		editEvent.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1).format(formatter));
		editEvent.setMaximumAge(40);
		editEvent.setMinimumAge(18);
		Event eventEdited = eventService.edit(1L, editEvent);

		assertEquals("title test edited", eventEdited.getTitle());
		assertEquals(150, eventEdited.getMaximumAge());
	}

	@Test
	public void shouldThrowBadRequestException_WhenEditEventLessThan24Hours() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusHours(4));
		event.setFinishDateTime(LocalDateTime.now().plusHours(5));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		EditEventWrapper editEvent = new EditEventWrapper();
		editEvent.setEventId(1L);
		editEvent.setTitle("title test edited");
		editEvent.setCity("Test City");
		editEvent.setAdditionalInfo("info test");
		editEvent.setCompanionGender(Gender.P);
		editEvent.setStartDateTime(LocalDateTime.now().plusDays(3).format(formatter));
		editEvent.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1).format(formatter));
		editEvent.setMaximumAge(40);
		editEvent.setMinimumAge(18);

		assertThatThrownBy(() -> eventService.edit(1L, editEvent))
		.hasMessageContaining("Error: The event will be started in less than 24 hours").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowResponseStatusException_WhenEditEventIsNotCreator() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusDays(3));
		event.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		EditEventWrapper editEvent = new EditEventWrapper();
		editEvent.setEventId(1L);
		editEvent.setTitle("title test edited");
		editEvent.setCity("Test City");
		editEvent.setAdditionalInfo("info test");
		editEvent.setCompanionGender(Gender.P);
		editEvent.setStartDateTime(LocalDateTime.now().plusDays(3).format(formatter));
		editEvent.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1).format(formatter));
		editEvent.setMaximumAge(40);
		editEvent.setMinimumAge(18);

		assertThatThrownBy(() -> eventService.edit(2L, editEvent))
				.hasMessageContaining("Error: Users are not authorized to edit this event").isInstanceOf(ResponseStatusException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenEditEventToMakeAgeLessThan18() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusDays(3));
		event.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		EditEventWrapper editEvent = new EditEventWrapper();
		editEvent.setEventId(1L);
		editEvent.setTitle("title test edited");
		editEvent.setCity("Test City");
		editEvent.setAdditionalInfo("info test");
		editEvent.setCompanionGender(Gender.P);
		editEvent.setStartDateTime(LocalDateTime.now().plusDays(3).format(formatter));
		editEvent.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1).format(formatter));
		editEvent.setMaximumAge(40);
		editEvent.setMinimumAge(16);

		assertThatThrownBy(() -> eventService.edit(1L, editEvent))
				.hasMessageContaining("Error: Minimum age must be 18!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenEditEventMaxAgeLessThanMinAge() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusDays(3));
		event.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		EditEventWrapper editEvent = new EditEventWrapper();
		editEvent.setEventId(1L);
		editEvent.setTitle("title test edited");
		editEvent.setCity("Test City");
		editEvent.setAdditionalInfo("info test");
		editEvent.setCompanionGender(Gender.P);
		editEvent.setStartDateTime(LocalDateTime.now().plusDays(3).format(formatter));
		editEvent.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1).format(formatter));
		editEvent.setMaximumAge(18);
		editEvent.setMinimumAge(40);

		assertThatThrownBy(() -> eventService.edit(1L, editEvent))
				.hasMessageContaining("Error: Inputted age is not valid!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenEditEventToStartTimeLessThan24Hours() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusDays(3));
		event.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		EditEventWrapper editEvent = new EditEventWrapper();
		editEvent.setEventId(1L);
		editEvent.setTitle("title test edited");
		editEvent.setCity("Test City");
		editEvent.setAdditionalInfo("info test");
		editEvent.setCompanionGender(Gender.P);
		editEvent.setStartDateTime(LocalDateTime.now().plusHours(10).format(formatter));
		editEvent.setFinishDateTime(LocalDateTime.now().plusHours(11).format(formatter));
		editEvent.setMaximumAge(40);
		editEvent.setMinimumAge(18);

		assertThatThrownBy(() -> eventService.edit(1L, editEvent))
				.hasMessageContaining("Error: Date inputted have to be after today!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenEditEventToFinishTimeEarlierThanStartTime() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusDays(3));
		event.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		EditEventWrapper editEvent = new EditEventWrapper();
		editEvent.setEventId(1L);
		editEvent.setTitle("title test edited");
		editEvent.setCity("Test City");
		editEvent.setAdditionalInfo("info test");
		editEvent.setCompanionGender(Gender.P);
		editEvent.setStartDateTime(LocalDateTime.now().plusDays(2).plusHours(1).format(formatter));
		editEvent.setFinishDateTime(LocalDateTime.now().plusDays(2).format(formatter));
		editEvent.setMaximumAge(40);
		editEvent.setMinimumAge(18);

		assertThatThrownBy(() -> eventService.edit(1L, editEvent))
				.hasMessageContaining("Error: Start time must be earlier than finish time!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenEditEventToStartTimeAndFinishTimeDifferentDay() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusDays(3));
		event.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
		Mockito.when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0, Event.class));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		EditEventWrapper editEvent = new EditEventWrapper();
		editEvent.setEventId(1L);
		editEvent.setTitle("title test edited");
		editEvent.setCity("Test City");
		editEvent.setAdditionalInfo("info test");
		editEvent.setCompanionGender(Gender.P);
		editEvent.setStartDateTime(LocalDateTime.now().plusDays(10).format(formatter));
		editEvent.setFinishDateTime(LocalDateTime.now().plusDays(11).format(formatter));
		editEvent.setMaximumAge(40);
		editEvent.setMinimumAge(18);

		assertThatThrownBy(() -> eventService.edit(1L, editEvent))
				.hasMessageContaining("Error: Start date and finish date must be the same day!").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void cancelEventSuccess() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusDays(3));
		event.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Applicant applicant = new Applicant();
		applicant.setEvent(event);
		applicant.setApplicantUser(user);
		applicant.setStatus(ApplicantStatus.APPLIED);

		Mockito.when(applicantRepository.findByApplicantUserIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.of(applicant));
		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);
		eventService.cancelEvent(1L, 1L);
		verify(applicantRepository, times(1)).delete(applicant);

	}

	@Test
	public void shouldThrowBadRequestException_WhenCancelEventRejected() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusDays(3));
		event.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Applicant applicant = new Applicant();
		applicant.setEvent(event);
		applicant.setApplicantUser(user);
		applicant.setStatus(ApplicantStatus.REJECTED);

		Mockito.when(applicantRepository.findByApplicantUserIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.of(applicant));
		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);

		assertThatThrownBy(() -> eventService.cancelEvent(1L, 1L))
				.hasMessageContaining("Error: You are already rejected. You don't need to cancel it anymore.").isInstanceOf(BadRequestException.class);

	}

	@Test
	public void shouldThrowBadRequestException_WhenCancelEventStartedLessThan24Hours() {
		event = new Event();
		event.setEventId(1L);
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now().plusHours(10));
		event.setFinishDateTime(LocalDateTime.now().plusHours(11));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Applicant applicant = new Applicant();
		applicant.setEvent(event);
		applicant.setApplicantUser(user);
		applicant.setStatus(ApplicantStatus.APPLIED);

		Mockito.when(applicantRepository.findByApplicantUserIdAndEventId(anyLong(), anyLong())).thenReturn(Optional.of(applicant));
		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

		ReflectionTestUtils.setField(eventService, "cancelationMax", (long) 86400000);

		assertThatThrownBy(() -> eventService.cancelEvent(1L, 1L))
				.hasMessageContaining("Error: The event will be started in less than 24 hours").isInstanceOf(BadRequestException.class);

	}

	@Test
	public void findMyEventSuccess() {
		EventFindAllListDBResponseWrapper event1 = new EventFindAllListDBResponseWrapper();
		event1.setEventId(1L);
		event1.setProfileId(1L);
		event1.setCreatorFullName("test");
		event1.setCreatedBy("test");
		event1.setPhotoProfileUrl("");
		event1.setTitle("Event Test 1");
		event1.setCity("Yogya");
		event1.setStartDateTime(LocalDateTime.now().plusDays(2));
		event1.setFinishDateTime(LocalDateTime.now().plusDays(2).plusHours(1));
		event1.setMinimumAge(18);
		event1.setMaximumAge(22);
		event1.setCreatorGender(Gender.L);
		event1.setCompanionGender(Gender.B);
		event1.setApplicantStatus(null);

		EventFindAllListDBResponseWrapper event2 = new EventFindAllListDBResponseWrapper();
		event2.setEventId(2L);
		event2.setProfileId(1L);
		event2.setCreatorFullName("test");
		event2.setCreatedBy("test");
		event2.setPhotoProfileUrl("");
		event2.setTitle("Event Test 2");
		event2.setCity("Yogya");
		event2.setStartDateTime(LocalDateTime.now().plusDays(3));
		event2.setFinishDateTime(LocalDateTime.now().plusDays(3).plusHours(1));
		event2.setMinimumAge(18);
		event2.setMaximumAge(22);
		event2.setCreatorGender(Gender.L);
		event2.setCompanionGender(Gender.B);
		event2.setApplicantStatus(null);

		List<EventFindAllListDBResponseWrapper> list = new ArrayList<>();
		list.add(event1);
		list.add(event2);

		profile = new Profile();

		Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		Mockito.when(eventRepository.findAllMyEvent(anyLong(), any(LocalDateTime.class), anyInt(), any(Sort.class))).thenReturn(list);
		Mockito.when(profileRepository.findById(anyLong())).thenReturn(Optional.of(profile));
		Mockito.when(applicantRepository.findByEventIdAccepted(anyLong())).thenReturn(new ArrayList<>());

		List<EventFindAllListDBResponseWrapper> response = eventService.findMyEvent("createdDate", "DESC", 1L, true);
		assertEquals("Event Test 1", response.get(0).getTitle());
	}

	@Test
	public void creatorCancelEventSuccess() {
		event = new Event();
		event.setUser(user);
		event.setFinishDateTime(LocalDateTime.now().plusHours(2));
		event.setStartDateTime(LocalDateTime.now().plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setCompanionGender(Gender.B);
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

		eventService.creatorCancelEvent(1L, 1L);
		verify(eventRepository, times(1)).save(any(Event.class));
	}
}
