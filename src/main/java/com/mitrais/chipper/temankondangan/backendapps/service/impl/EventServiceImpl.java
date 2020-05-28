package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.mitrais.chipper.temankondangan.backendapps.model.json.*;
import com.mitrais.chipper.temankondangan.backendapps.service.ImageFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Entity;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ApplicantResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListDBResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.EventService;

@Service
public class EventServiceImpl implements EventService {

	private static final String ERROR_SORT_DIRECTION = "Error: Can only input ASC or DESC for direction!";

	private EventRepository eventRepository;
	private UserRepository userRepository;
	private ProfileRepository profileRepository;
	private ApplicantRepository applicantRepository;
	private ImageFileService imageFileService;

	@Value("${app.eventCancelationValidMaxMsec}")
	Long cancelationMax;

	@Autowired
	public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
			ApplicantRepository applicantRepository, ProfileRepository profileRepository,
			ImageFileService imageFileService) {
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
		this.applicantRepository = applicantRepository;
		this.profileRepository = profileRepository;
		this.imageFileService = imageFileService;
	}

	@Override
	public Event create(Long userId, CreateEventWrapper wrapper) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		if (wrapper.getMinimumAge() < 18) {
			throw new BadRequestException("Error: Minimum age must be 18!");
		}

		if (wrapper.getMaximumAge() < wrapper.getMinimumAge()) {
			throw new BadRequestException("Error: Inputted age is not valid!");
		}

		// check dateAndTime valid
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
		LocalDateTime startDateTime;
		LocalDateTime finishDateTime = null;
		startDateTime = LocalDateTime.parse(wrapper.getStartDateTime(), df);

		if (startDateTime.isBefore(LocalDateTime.now().plusDays(1))) {
			throw new BadRequestException("Error: Date inputted have to be after today!");
		}

		if (!StringUtils.isEmpty(wrapper.getFinishDateTime())) {

			finishDateTime = LocalDateTime.parse(wrapper.getFinishDateTime(), df);
			if (startDateTime.isAfter(finishDateTime)) {
				throw new BadRequestException("Error: Start time must be earlier than finish time!");
			}
			if (!startDateTime.toLocalDate().isEqual(finishDateTime.toLocalDate())) {
				throw new BadRequestException("Error: Start date and finish date must be the same day!");
			}

		}

		int maxAge = wrapper.getMaximumAge();
		if (maxAge >= 40) {
			maxAge = 150;
		}

		Event event = new Event();
		event.setUser(user);
		event.setTitle(wrapper.getTitle());
		event.setCity(wrapper.getCity());
		event.setStartDateTime(startDateTime);
		event.setFinishDateTime(finishDateTime);
		event.setCompanionGender(wrapper.getCompanionGender());
		event.setMinimumAge(wrapper.getMinimumAge());
		event.setMaximumAge(maxAge);
		event.setAdditionalInfo(wrapper.getAdditionalInfo());
		event.setDataState(DataState.ACTIVE);

		return eventRepository.save(event);

	}

	@Override
	public EventFindAllResponseWrapper findAll(Integer pageNumber, Integer pageSize, String sortBy, String direction,
			Long userId) {

		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.PROFILE.getLabel(), "userId", userId));
		Integer age = Period.between(profile.getDob(), LocalDate.now()).getYears();
		ArrayList<Gender> gender = new ArrayList<>();
		gender.add(Gender.B);
		gender.add(profile.getGender());

		if (!("createdDate".equals(sortBy) || "startDateTime".equals(sortBy))) {
			throw new BadRequestException("Error: Can only input createdDate or startDateTime for sortBy!");
		}

		Pageable paging;
		if (direction.equalsIgnoreCase("DESC")) {
			paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());

		} else if (direction.equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());

		} else {
			throw new BadRequestException(ERROR_SORT_DIRECTION);
		}

		Page<EventFindAllListDBResponseWrapper> eventWrapperPages = eventRepository.findAllByRelevantInfo(age, gender,
				userId, LocalDateTime.now(), paging);
		List<EventFindAllListDBResponseWrapper> eventAllDBResponse = new ArrayList<>();
		eventWrapperPages.forEach(eventWrap -> {
			AtomicReference<String> photoProfileUrl = new AtomicReference<>("");
			profileRepository.findById(eventWrap.getProfileId())
					.ifPresent(profileCreator -> photoProfileUrl.set(imageFileService.getImageUrl(profileCreator)));

			eventWrap.setPhotoProfileUrl(photoProfileUrl.get());
			eventAllDBResponse.add(eventWrap);
		});

		return EventFindAllResponseWrapper.builder().pageNumber(pageNumber).pageSize(pageSize)
				.actualSize(eventWrapperPages.getTotalElements()).contentList(eventAllDBResponse).build();

	}

	@Override
	public List<EventFindAllListDBResponseWrapper> findMyEvent(String sortBy, String direction, Long userId,
			boolean current) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "userId", userId));

		if (!("createdDate".equals(sortBy) || "startDateTime".equals(sortBy))) {
			throw new BadRequestException("Error: Can only input createdDate or startDateTime for sortBy!");
		}

		Sort sort;
		if (direction.equalsIgnoreCase("DESC")) {
			sort = Sort.by(sortBy).descending();

		} else if (direction.equalsIgnoreCase("ASC")) {
			sort = Sort.by(sortBy).ascending();

		} else {
			throw new BadRequestException(ERROR_SORT_DIRECTION);
		}

		List<EventFindAllListDBResponseWrapper> eventWrapperPages = eventRepository.findAllMyEvent(user.getUserId(),
				LocalDateTime.now(), current ? 1 : 0, sort);

		List<EventFindAllListDBResponseWrapper> eventAllDBResponse = new ArrayList<>();
		eventWrapperPages.forEach(eventWrap -> {
			AtomicReference<String> photoProfileUrl = new AtomicReference<>("");
			profileRepository.findById(eventWrap.getProfileId())
					.ifPresent(profileCreator -> photoProfileUrl.set(imageFileService.getImageUrl(profileCreator)));

			eventWrap.setPhotoProfileUrl(photoProfileUrl.get());
			eventAllDBResponse.add(eventWrap);
		});

		return eventAllDBResponse;
	}

	@Override
	public Event edit(Long userId, EditEventWrapper wrapper) {
		Event event = eventRepository.findById(wrapper.getEventId())
				.orElseThrow(() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", wrapper.getEventId()));

		if (!isCancelationValid(event.getStartDateTime())) {
			throw new BadRequestException("Error: The event will be started in less than 24 hours");
		}

		if (!event.getUser().getUserId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"Error: Users are not authorized to edit this event");
		}

		if (wrapper.getMinimumAge() < 18) {
			throw new BadRequestException("Error: Minimum age must be 18!");
		}

		if (wrapper.getMaximumAge() < wrapper.getMinimumAge()) {
			throw new BadRequestException("Error: Inputted age is not valid!");
		}

		// check dateAndTime valid
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
		LocalDateTime startDateTime;
		LocalDateTime finishDateTime = null;
		startDateTime = LocalDateTime.parse(wrapper.getStartDateTime(), df);

		if (startDateTime.isBefore(LocalDateTime.now().plusDays(1))) {
			throw new BadRequestException("Error: Date inputted have to be after today!");
		}

		if (!StringUtils.isEmpty(wrapper.getFinishDateTime())) {

			finishDateTime = LocalDateTime.parse(wrapper.getFinishDateTime(), df);
			if (startDateTime.isAfter(finishDateTime)) {
				throw new BadRequestException("Error: Start time must be earlier than finish time!");
			}
			if (!startDateTime.toLocalDate().isEqual(finishDateTime.toLocalDate())) {
				throw new BadRequestException("Error: Start date and finish date must be the same day!");
			}

		}

		int maxAge = wrapper.getMaximumAge();
		if (maxAge >= 40) {
			maxAge = 150;
		}

		event.setTitle(wrapper.getTitle());
		event.setCity(wrapper.getCity());
		event.setStartDateTime(startDateTime);
		event.setFinishDateTime(finishDateTime);
		event.setCompanionGender(wrapper.getCompanionGender());
		event.setMinimumAge(wrapper.getMinimumAge());
		event.setMaximumAge(maxAge);
		event.setAdditionalInfo(wrapper.getAdditionalInfo());

		return eventRepository.save(event);

	}

	@Override
	public EventDetailResponseWrapper findEventDetail(String eventIdStr, Long userId) {
		List<ApplicantResponseWrapper> applicantResponseWrapperList = new ArrayList<>();
		boolean isApplied = false;
		Long id;

		// Custom exception as requested by Tester, when input param.
		try {
			id = Long.parseLong(eventIdStr);
		} catch (NumberFormatException ex) {
			throw new BadRequestException(
					"Error: Cannot use the text value as parameter, please use the number format value!");
		}

		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", id));

		User userCreator = userRepository.findById(event.getUser().getUserId()).orElseThrow(
				() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", event.getUser().getUserId()));

		Profile profileCreator = profileRepository.findByUserId(userCreator.getUserId()).orElseThrow(
				() -> new ResourceNotFoundException(Entity.PROFILE.getLabel(), "id", userCreator.getUserId()));

		if (userId.equals(userCreator.getUserId())) {
			applicantRepository.findByEventId(event.getEventId()).forEach(applicant -> {
				Profile profileApplicant = profileRepository.findByUserId(applicant.getApplicantUser().getUserId())
						.orElseThrow(() -> new ResourceNotFoundException(Entity.PROFILE.getLabel(), "id",
								applicant.getApplicantUser().getUserId()));

				applicantResponseWrapperList.add(ApplicantResponseWrapper.builder().applicantId(applicant.getId())
						.fullName(profileApplicant.getFullName()).userId(applicant.getApplicantUser().getUserId())
						.status(applicant.getStatus()).build());
			});
		} else {
			User userApplicant = userRepository.findById(userId).orElseThrow(
					() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", event.getUser().getUserId()));
			isApplied = applicantRepository.existsByApplicantUserAndEvent(userApplicant, event);
		}

		String photoProfileUrl = imageFileService.getImageUrl(profileCreator);

		return EventDetailResponseWrapper.builder().eventId(event.getEventId()).creatorUserId(userCreator.getUserId())
				.fullName(profileCreator.getFullName()).photoProfileUrl(photoProfileUrl).title(event.getTitle())
				.city(event.getCity()).startDateTime(event.getStartDateTime()).finishDateTime(event.getFinishDateTime())
				.minimumAge(event.getMinimumAge()).maximumAge(event.getMaximumAge())
				.companionGender(event.getCompanionGender()).additionalInfo(event.getAdditionalInfo())
				.applicantList(applicantResponseWrapperList).isCreator(userId.equals(userCreator.getUserId()))
				.isApplied(isApplied).build();
	}

	@Override
	public void apply(Long userId, Long eventId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", eventId));

		if (user.getUserId().equals(event.getUser().getUserId())) {
			throw new BadRequestException("Error: You cannot apply to your own event!");
		}

		if (Boolean.TRUE.equals(applicantRepository.existsByApplicantUserAndEvent(user, event))) {
			throw new BadRequestException("Error: You have applied to this event");
		}

		if ((event.getFinishDateTime() != null && LocalDateTime.now().isAfter(event.getFinishDateTime()))
				|| LocalDateTime.now().isAfter(event.getStartDateTime())) {
			throw new BadRequestException("Error: This event has finished already");
		}

		Applicant applicant = new Applicant();
		applicant.setApplicantUser(user);
		applicant.setEvent(event);
		applicant.setDataState(DataState.ACTIVE);
		applicant.setStatus(ApplicantStatus.APPLIED);
		applicantRepository.save(applicant);
	}

	@Override
	public void cancelEvent(Long userApplicantId, Long eventId) {
		Applicant applicant = applicantRepository.findByApplicantUserIdAndEventId(userApplicantId, eventId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.APPLICANT.getLabel(), "eventId", eventId));
		Event event = eventRepository.findById(applicant.getEvent().getEventId()).orElseThrow(
				() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", applicant.getEvent().getEventId()));

		if (isCancelationValid(event.getStartDateTime())) {
			applicantRepository.delete(applicant);
		} else {
			throw new BadRequestException("Error: The event will be started in less than 24 hours");
		}

	}

	@Override
	public List<AppliedEventWrapper> findActiveAppliedEvent(Long userId, String sortBy, String direction) {
		List<AppliedEventWrapper> resultList = new ArrayList<>();

		Sort sort;
		if (direction.equalsIgnoreCase("DESC")) {
			sort = Sort.by(sortBy).descending();
		} else if (direction.equalsIgnoreCase("ASC")) {
			sort = Sort.by(sortBy).ascending();
		} else {
			throw new BadRequestException(ERROR_SORT_DIRECTION);
		}

		eventRepository.findAppliedEvent(userId, DataState.ACTIVE, LocalDateTime.now(), 1, sort).forEach(event -> {
			AppliedEventWrapper wrapper = new AppliedEventWrapper();
			wrapper.setTitle(event.getTitle());
			wrapper.setCity(event.getCity());
			wrapper.setStartDateTime(event.getStartDateTime());
			wrapper.setFinishDateTime(event.getFinishDateTime());

			profileRepository.findByUserId(event.getUser().getUserId())
					.ifPresent(profile -> wrapper.setPhotoProfileUrl(imageFileService.getImageUrl(profile)));

			applicantRepository.findByApplicantUserIdAndEventId(userId, event.getEventId())
					.ifPresent(applicant -> wrapper.setStatus(applicant.getStatus()));

			resultList.add(wrapper);
		});

		return resultList;
	}

	@Override
	public List<AppliedEventWrapper> findPastAppliedEvent(Long userId, String sortBy, String direction) {
		List<AppliedEventWrapper> resultList = new ArrayList<>();

		Sort sort;
		if (direction.equalsIgnoreCase("DESC")) {
			sort = Sort.by(sortBy).descending();
		} else if (direction.equalsIgnoreCase("ASC")) {
			sort = Sort.by(sortBy).ascending();
		} else {
			throw new BadRequestException(ERROR_SORT_DIRECTION);
		}

		eventRepository.findAppliedEvent(userId, DataState.ACTIVE, LocalDateTime.now(), 0, sort).forEach(event -> {
			System.out.println(event);

			AppliedEventWrapper wrapper = new AppliedEventWrapper();
			wrapper.setTitle(event.getTitle());
			wrapper.setCity(event.getCity());
			wrapper.setStartDateTime(event.getStartDateTime());
			wrapper.setFinishDateTime(event.getFinishDateTime());

			profileRepository.findByUserId(event.getUser().getUserId())
					.ifPresent(profile -> wrapper.setPhotoProfileUrl(imageFileService.getImageUrl(profile)));

			applicantRepository.findByApplicantUserIdAndEventId(userId, event.getEventId())
					.ifPresent(applicant -> wrapper.setStatus(applicant.getStatus()));

			resultList.add(wrapper);
		});

		return resultList;
	}

	private boolean isCancelationValid(LocalDateTime eventDate) {
		Duration duration = Duration.between(LocalDateTime.now(), eventDate);

		return duration.getSeconds() * 1000 > cancelationMax;
	}

}
