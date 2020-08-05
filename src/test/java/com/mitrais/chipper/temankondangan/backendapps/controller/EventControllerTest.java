package com.mitrais.chipper.temankondangan.backendapps.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.AppliedEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.EventServiceImpl;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventControllerTest {
	private MockMvc mockMvc;
	private WebApplicationContext context;
	private AuthenticationManager authenticationManager;
	private TokenProvider tokenProvider;
	private PasswordEncoder passwordEncoder;

	private String token;
	private User user;
	private final String email = "test@mail.com";
	private final String password = "test123!";
	private static DateTimeFormatter dfDateTime = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm")
			.withResolverStyle(ResolverStyle.STRICT);

	@MockBean
	UserRepository userRepository;

	@MockBean
	EventServiceImpl eventService;

	@Autowired
	public EventControllerTest(WebApplicationContext context, AuthenticationManager authenticationManager,
			TokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
		this.context = context;
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
		this.passwordEncoder = passwordEncoder;
	}

	@BeforeAll
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@BeforeEach
	public void initTest() {
		user = User.builder().userId(1L).email(email).passwordHashed(passwordEncoder.encode(password)).build();

		Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		token = tokenProvider.createToken(authentication);
	}

	@Test
	public void createEventTest() throws Exception {
		CreateEventWrapper wrapper = new CreateEventWrapper();
		wrapper.setAdditionalInfo("info test");
		wrapper.setCompanionGender(Gender.P);
		wrapper.setStartDateTime(LocalDateTime.now().plusDays(3).format(dfDateTime));
		wrapper.setFinishDateTime(LocalDateTime.now().plusDays(3).format(dfDateTime));
		wrapper.setMaximumAge(25);
		wrapper.setMinimumAge(18);
		wrapper.setTitle("title test");
		wrapper.setCity("Test City");

		Event event = new Event();
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now());
		event.setFinishDateTime(LocalDateTime.now().plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventService.create(Mockito.anyLong(), Mockito.any(CreateEventWrapper.class))).thenReturn(event);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/event/create")
				.header("Authorization", "Bearer " + token).content(asJsonString(wrapper))
				.contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content.title").value("title test"));
	}

	@Test
	public void shouldThrowNullPointerException_inCreateEventTest() throws Exception {
		Mockito.when(eventService.create(Mockito.anyLong(), Mockito.any(CreateEventWrapper.class)))
				.thenThrow(NullPointerException.class);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/event/create")
				.header("Authorization", "Bearer " + token).content(asJsonString(new CreateEventWrapper()))
				.contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.message").value("Error: Cannot send null values!"));
	}

	@Test
	public void shouldThrowDateTimeParseException_inCreateEventTest() throws Exception {
		Mockito.when(eventService.create(Mockito.anyLong(), Mockito.any(CreateEventWrapper.class)))
				.thenThrow(DateTimeParseException.class);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/event/create")
				.header("Authorization", "Bearer " + token).content(asJsonString(new CreateEventWrapper()))
				.contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.message").value("Error: 'null' is not a valid format"));
	}

	@Test
	public void shouldThrowTransactionSystemException_inCreateEventTest() throws Exception {
		Mockito.when(eventService.create(Mockito.anyLong(), Mockito.any(CreateEventWrapper.class)))
				.thenThrow(TransactionSystemException.class);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/event/create")
				.header("Authorization", "Bearer " + token).content(asJsonString(new CreateEventWrapper()))
				.contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.status").value(500));
	}

	@Test
	public void shouldThrowNumberFormatException_inCreateEventTest() throws Exception {
		Mockito.when(eventService.create(Mockito.anyLong(), Mockito.any(CreateEventWrapper.class)))
				.thenThrow(NumberFormatException.class);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/event/create")
				.header("Authorization", "Bearer " + token).content(asJsonString(new CreateEventWrapper()))
				.contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	public void findAllEventTest() throws Exception {
		EventFindAllListDBResponseWrapper event = new EventFindAllListDBResponseWrapper();
		event.setProfileId(1L);
		event.setEventId(2L);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setCreatorFullName("creator name test");
		event.setCreatedBy("system");
		event.setCity("city test");
		event.setStartDateTime(LocalDateTime.now());
		event.setFinishDateTime(LocalDateTime.now());
		event.setMinimumAge(18);
		event.setMaximumAge(40);
		event.setCreatorGender(Gender.B);
		event.setCompanionGender(Gender.L);
		event.setApplicantStatus(ApplicantStatus.ACCEPTED);
		event.setHasAcceptedApplicant(true);
		event.setCancelled(false);

		List<EventFindAllListDBResponseWrapper> eventList = Arrays.asList(event);

		EventFindAllResponseWrapper responseWrapper = EventFindAllResponseWrapper.builder().actualSize(1).pageNumber(0)
				.pageSize(10).contentList(eventList).build();
		Mockito.when(eventService.findAll(any(Integer.class), any(Integer.class), any(String.class), any(String.class),
				anyLong())).thenReturn(responseWrapper);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/event/find-all")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content.contentList[0].title").value("title test"));
	}

	@Test
	public void editEventTest() throws Exception {
		EditEventWrapper wrapper = new EditEventWrapper();
		wrapper.setAdditionalInfo("info test");
		wrapper.setCompanionGender(Gender.P);
		wrapper.setStartDateTime(LocalDateTime.now().plusDays(3).format(dfDateTime));
		wrapper.setFinishDateTime(LocalDateTime.now().plusDays(3).format(dfDateTime));
		wrapper.setMaximumAge(25);
		wrapper.setMinimumAge(18);
		wrapper.setTitle("title test");
		wrapper.setCity("Test City");

		Event event = new Event();
		event.setUser(user);
		event.setAdditionalInfo("info test");
		event.setCompanionGender(Gender.P);
		event.setStartDateTime(LocalDateTime.now());
		event.setFinishDateTime(LocalDateTime.now().plusHours(1));
		event.setMaximumAge(40);
		event.setMinimumAge(18);
		event.setTitle("title test");
		event.setCity("Test City");
		event.setDataState(DataState.ACTIVE);

		Mockito.when(eventService.edit(anyLong(), Mockito.any(EditEventWrapper.class))).thenReturn(event);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/event/edit")
				.header("Authorization", "Bearer " + token).content(asJsonString(wrapper))
				.contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content.title").value("title test"));
	}

//	@Test
//	public void findEventDetailTest() throws Exception {
//		EventDetailResponseWrapper responseWrapper = EventDetailResponseWrapper.builder().title("title test").build();
//
//		Mockito.when(eventService.findEventDetail(any(String.class), anyLong())).thenReturn(responseWrapper);
//		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/event/find").param("eventId", Mockito.anyString())
//				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
//				.accept(MediaType.APPLICATION_JSON);
//
//		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
//				.andExpect(jsonPath("$.content.title").value("title test"));
//	}

	@Test
	public void applyEventTest() throws Exception {
		Mockito.doNothing().when(eventService).apply(anyLong(), anyLong());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/event/apply?eventId=1")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").value("Successfully applied to the event"));
	}

	@Test
	public void cancelEventTest() throws Exception {
		Mockito.doNothing().when(eventService).cancelEvent(anyLong(), anyLong());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/event/cancel?eventId=1")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").value("The event was canceled successfully"));
	}

	@Test
	public void findMyCurrentEventTest() throws Exception {
		EventFindAllListDBResponseWrapper wrapper = EventFindAllListDBResponseWrapper.builder().title("title test")
				.build();
		List<EventFindAllListDBResponseWrapper> wrapperList = Arrays.asList(wrapper);

		Mockito.when(eventService.findMyEvent(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyBoolean())).thenReturn(wrapperList);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/event/my-event-current")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[0].title").value("title test"));
	}

	@Test
	public void findMyPastEventTest() throws Exception {
		EventFindAllListDBResponseWrapper wrapper = EventFindAllListDBResponseWrapper.builder().title("title test")
				.build();
		List<EventFindAllListDBResponseWrapper> wrapperList = Arrays.asList(wrapper);

		Mockito.when(eventService.findMyEvent(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyBoolean())).thenReturn(wrapperList);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/event/my-event-past")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[0].title").value("title test"));
	}

	@Test
	public void findCurrentAppliedEventTest() throws Exception {
		AppliedEventWrapper appliedEventWrapper = AppliedEventWrapper.builder().photoProfileUrl("image.jpg")
				.title("Lorem Ipsum").startDateTime(LocalDateTime.now().plusDays(1))
				.finishDateTime(LocalDateTime.now().plusDays(1).plusHours(1)).city("Sim City")
				.applicantStatus(ApplicantStatus.APPLIED).build();

		List<AppliedEventWrapper> wrapperList = Arrays.asList(appliedEventWrapper);

		Mockito.when(eventService.findActiveAppliedEvent(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(wrapperList);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/event/my-applied-event-current")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[0].photoProfileUrl").value("image.jpg"))
				.andExpect(jsonPath("$.content[0].title").value("Lorem Ipsum"));
	}

	@Test
	public void findPastAppliedEventTest() throws Exception {
		AppliedEventWrapper appliedEventWrapper = AppliedEventWrapper.builder().photoProfileUrl("image.jpg")
				.title("Lorem Ipsum").startDateTime(LocalDateTime.now().minusDays(1))
				.finishDateTime(LocalDateTime.now().minusDays(1).plusHours(1)).city("Sim City")
				.applicantStatus(ApplicantStatus.APPLIED).build();

		List<AppliedEventWrapper> wrapperList = Arrays.asList(appliedEventWrapper);

		Mockito.when(eventService.findPastAppliedEvent(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(wrapperList);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/event/my-applied-event-past")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[0].photoProfileUrl").value("image.jpg"))
				.andExpect(jsonPath("$.content[0].title").value("Lorem Ipsum"));
	}

	@Test
	public void creatorCancelEventTest() throws Exception {
		Mockito.doNothing().when(eventService).creatorCancelEvent(anyLong(), anyLong());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/event/creator-cancel?eventId=1")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").value("The event was canceled successfully"));
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
