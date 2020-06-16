
package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.mitrais.chipper.temankondangan.backendapps.exception.UnauthorizedException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Entity;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.AcceptedApplicantResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ApplicantResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.AppliedEventWrapper;
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
import com.mitrais.chipper.temankondangan.backendapps.service.ImageFileService;

@Service
public class EventServiceImpl implements EventService {
	private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
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
		event.setCancelled(false);

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
			eventWrap.setHasAcceptedApplicant(
					!applicantRepository.findByEventIdAccepted(eventWrap.getEventId()).isEmpty());
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
			eventWrap.setHasAcceptedApplicant(
					!applicantRepository.findByEventIdAccepted(eventWrap.getEventId()).isEmpty());
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
		ApplicantStatus applicantStatus = null;
		AcceptedApplicantResponseWrapper acceptedApplicant = new AcceptedApplicantResponseWrapper();
		Boolean hasAcceptedApplicant = null;

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
			applicantRepository.findByEventId(event.getEventId()).ifPresent(a -> a.forEach(applicant -> {
				Profile profileApplicant = profileRepository.findByUserId(applicant.getApplicantUser().getUserId())
						.orElseThrow(() -> new ResourceNotFoundException(Entity.PROFILE.getLabel(), "id",
								applicant.getApplicantUser().getUserId()));

				applicantResponseWrapperList.add(ApplicantResponseWrapper.builder().applicantId(applicant.getId())
						.fullName(profileApplicant.getFullName()).userId(applicant.getApplicantUser().getUserId())
						.status(applicant.getStatus()).build());

				if (applicant.getStatus().compareTo(ApplicantStatus.ACCEPTED) == 0) {
					acceptedApplicant.setUserId(profileApplicant.getUser().getUserId());
					acceptedApplicant.setFullName(profileApplicant.getFullName());
					acceptedApplicant.setGender(profileApplicant.getGender());
					acceptedApplicant.setPhotoProfileUrl(imageFileService.getImageUrl(profileApplicant));
				}
			}));
		} else {
			User userApplicant = userRepository.findById(userId).orElseThrow(
					() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", event.getUser().getUserId()));
			isApplied = applicantRepository.existsByApplicantUserAndEvent(userApplicant, event);
			Optional<Applicant> applicantOpt = applicantRepository.findByApplicantUserIdAndEventId(userId, id);
			if (applicantOpt.isPresent()) {
				applicantStatus = applicantOpt.get().getStatus();
			}
		}

		String photoProfileUrl = imageFileService.getImageUrl(profileCreator);

		if (StringUtils.isNotEmpty(acceptedApplicant.getFullName())) {
			hasAcceptedApplicant = true;
		}

		return EventDetailResponseWrapper.builder().eventId(event.getEventId()).creatorUserId(userCreator.getUserId())
				.fullName(profileCreator.getFullName()).photoProfileUrl(photoProfileUrl).title(event.getTitle())
				.city(event.getCity()).startDateTime(event.getStartDateTime()).finishDateTime(event.getFinishDateTime())
				.minimumAge(event.getMinimumAge()).maximumAge(event.getMaximumAge())
				.companionGender(event.getCompanionGender()).additionalInfo(event.getAdditionalInfo())
				.applicantList(applicantResponseWrapperList).isCreator(userId.equals(userCreator.getUserId()))
				.isApplied(isApplied).applicantStatus(applicantStatus).hasAcceptedApplicant(hasAcceptedApplicant)
				.acceptedApplicant(acceptedApplicant).cancelled(event.getCancelled()).build();
	}

	@Override
	public void apply(Long userId, Long eventId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", eventId));

		Profile profile = profileRepository.findByUserId(user.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(Entity.PROFILE.getLabel(), "id", user.getUserId()));

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

		int userAge = profile.getDob().until(LocalDate.now()).getYears();
		if (userAge > event.getMaximumAge() || userAge < event.getMinimumAge()) {
			throw new BadRequestException("Error: Your age does not meet the requirement");
		}

		if (!event.getCompanionGender().equals(Gender.B)
				&& event.getCompanionGender().compareTo(profile.getGender()) != 0) {
			throw new BadRequestException("Error: Your gender does not meet the requirement");
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

		if (applicant.getStatus().equals(ApplicantStatus.REJECTED)) {
			throw new BadRequestException("Error: You are already rejected. You don't need to cancel it anymore.");
		}

		if (isCancelationValid(event.getStartDateTime())) {
			applicantRepository.delete(applicant);
		} else {
			throw new BadRequestException("Error: The event will be started in less than 24 hours");
		}

	}

	@Override

	public void creatorCancelEvent(Long userId, Long eventId) {
		if (eventId == null) {
			throw new BadRequestException("Error: eventId cannot null");
		}

		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", eventId));

		if (!userId.equals(event.getUser().getUserId())) {
			throw new UnauthorizedException("Error: Users are not authorized to cancel this event");
		}

		if (Boolean.TRUE.equals(event.getCancelled())) {
			throw new BadRequestException("Error: You already have canceled this event");
		}

		if (isCancelationValid(event.getStartDateTime())) {
			event.setCancelled(true);
			eventRepository.save(event);
		} else {
			throw new BadRequestException("Error: The event will be started in less than 24 hours");
		}
	}

	@Override
	public List<AppliedEventWrapper> findActiveAppliedEvent(Long userId, String sortBy, String direction) {
		List<AppliedEventWrapper> resultList = new ArrayList<>();

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

		eventRepository.findAppliedEvent(userId, DataState.ACTIVE, LocalDateTime.now(), 1, sort).forEach(event -> {
			AppliedEventWrapper wrapper = new AppliedEventWrapper();
			wrapper.setEventId(event.getEventId());
			wrapper.setTitle(event.getTitle());
			wrapper.setCity(event.getCity());
			wrapper.setStartDateTime(event.getStartDateTime());
			wrapper.setFinishDateTime(event.getFinishDateTime());
			wrapper.setCancelled(event.getCancelled());

			profileRepository.findByUserId(event.getUser().getUserId()).ifPresent(profile -> {
				wrapper.setPhotoProfileUrl(imageFileService.getImageUrl(profile));
				wrapper.setCreatorFullName(profile.getFullName());
				wrapper.setCreatorGender(profile.getGender());
			});

			applicantRepository.findByApplicantUserIdAndEventId(userId, event.getEventId())
					.ifPresent(applicant -> wrapper.setApplicantStatus(applicant.getStatus()));

			resultList.add(wrapper);
		});

		return resultList;
	}

	@Override
	public List<AppliedEventWrapper> findPastAppliedEvent(Long userId, String sortBy, String direction) {
		List<AppliedEventWrapper> resultList = new ArrayList<>();

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

		eventRepository.findAppliedEvent(userId, DataState.ACTIVE, LocalDateTime.now(), 0, sort).forEach(event -> {
			logger.info(event.toString());

			AppliedEventWrapper wrapper = new AppliedEventWrapper();
			wrapper.setEventId(event.getEventId());
			wrapper.setTitle(event.getTitle());
			wrapper.setCity(event.getCity());
			wrapper.setStartDateTime(event.getStartDateTime());
			wrapper.setFinishDateTime(event.getFinishDateTime());
			wrapper.setCancelled(event.getCancelled());

			profileRepository.findByUserId(event.getUser().getUserId()).ifPresent(profile -> {
				wrapper.setPhotoProfileUrl(imageFileService.getImageUrl(profile));
				wrapper.setCreatorFullName(profile.getFullName());
				wrapper.setCreatorGender(profile.getGender());
			});

			applicantRepository.findByApplicantUserIdAndEventId(userId, event.getEventId())
					.ifPresent(applicant -> wrapper.setApplicantStatus(applicant.getStatus()));

			resultList.add(wrapper);
		});

		return resultList;
	}

	private boolean isCancelationValid(LocalDateTime eventDate) {
		Duration duration = Duration.between(LocalDateTime.now(), eventDate);

		return duration.getSeconds() * 1000 > cancelationMax;
	}

	@Override
	public EventFindAllResponseWrapper search(Long userId, Integer pageNumber, Integer pageSize, String sortBy,
			String direction, Gender creatorGender, Integer creatorMaximumAge, Integer creatorMinimumAge,
			String startDate, String finishDate, List<String> startHour, List<String> finishHour, List<String> city) {
		// check sortBy and direction
		if (!("createdDate".equals(sortBy) || "startDateTime".equals(sortBy))) {
			throw new BadRequestException("Error: Can only input createdDate or startDateTime for sortBy!");
		} else if (sortBy.equals("startDateTime")) {
			sortBy = "start_date_time";
		} else {
			sortBy = "created_date";
		}

		Pageable paging;
		if (direction.equalsIgnoreCase("DESC")) {
			paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
		} else if (direction.equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
		} else {
			throw new BadRequestException(ERROR_SORT_DIRECTION);
		}

		// check age inputted
		if (creatorMinimumAge < 18) {
			throw new BadRequestException("Error: Minimum age must be 18!");
		}
		if (creatorMaximumAge < creatorMinimumAge) {
			throw new BadRequestException("Error: Inputted age is not valid!");
		}

		// check user profile who is searching
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.PROFILE.getLabel(), "userId", userId));
		Integer userAge = Period.between(profile.getDob(), LocalDate.now()).getYears();
		String companionGender = profile.getGender().toString();

		// check inputted gender in search
		List<String> creatorGenderSearch = new ArrayList<>();
		if (creatorGender.compareTo(Gender.B) == 0) {
			creatorGenderSearch.add("L");
			creatorGenderSearch.add("P");
		} else {
			creatorGenderSearch.add(creatorGender.toString());
		}

		// check inputted city in search
		String eventCity = "%%";
		StringBuilder builder = new StringBuilder();
		if (!(city == null || city.isEmpty())) {
			for (int i = 0; i < city.size(); i++) {
				if (i > 0) {
					builder.append("|");
				}
				builder.append("%").append(city.get(i).toLowerCase()).append("%");
			}
			eventCity = builder.toString();
		}

//				check startDate and finishDate
		if ((StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(finishDate))
				|| (StringUtils.isNotEmpty(startDate) && StringUtils.isEmpty(finishDate))) {
			throw new BadRequestException("Error: startDate and finishDate must be all empty or all filled!");
		}

		DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
		LocalDateTime startDateSearch = LocalDateTime.now();
		LocalDateTime finishDateSearch = LocalDateTime.now().plusDays(90);

		if ((StringUtils.isNotEmpty(startDate))) {
			startDateSearch = LocalDate.parse(startDate, dfDate).atStartOfDay();
			finishDateSearch = LocalDate.parse(finishDate, dfDate).atTime(LocalTime.MAX);

			if (startDateSearch.isBefore(LocalDateTime.now())) {
				throw new BadRequestException("Error: Date inputted have to be today or after!");
			}

			if (startDateSearch.isAfter(finishDateSearch)) {
				throw new BadRequestException("Error: startDate must be earlier than finishDate!");
			}
		}

		Integer startHourLowerRange = 0;
		Integer startHourUpperRange = 24;
		Integer finishHourLowerRange = 0;
		Integer finishHourUpperRange = 0;

		// maximum hour is 24, initialized in 25 so it won't get any event if not needed
		Integer secondStartHourLowerRange = 25;
		Integer secondStartHourUpperRange = 25;
		Integer secondFinishHourLowerRange = 25;
		Integer secondFinishHourUpperRange = 25;

		String hour1 = "00-12";
		String hour2 = "12-18";
		String hour3 = "18-00";

		// check startHour and finishHour
		if (!(startHour == null || startHour.isEmpty())) {
			int startHourSize = startHour.size();
			Collections.sort(startHour);

//					if startHour only contains "00-12", "18-24"
			if (startHourSize == 2 && startHour.get(0).equalsIgnoreCase(hour1)
					&& startHour.get(1).equalsIgnoreCase(hour3)) {
				startHourLowerRange = 0;
				startHourUpperRange = 12;
				secondStartHourLowerRange = 18;
				secondStartHourUpperRange = 24;

			} else {
				for (int i = 0; i < startHourSize; i++) {
					if (startHour.get(i).equalsIgnoreCase(hour1)) {
						startHourLowerRange = 0;
						startHourUpperRange = 12;
					} else if (startHour.get(i).equalsIgnoreCase(hour2)) {
						if (i == 0)
							startHourLowerRange = 12;
						startHourUpperRange = 18;
					} else if (startHour.get(i).equalsIgnoreCase(hour3)) {
						if (i == 0)
							startHourLowerRange = 18;
						startHourUpperRange = 24;
					} else {
						throw new BadRequestException("Error: Please use 00-12, 12-18 or 18-00 for hour value");
					}
				}
			}
		}

		if (!(finishHour == null || finishHour.isEmpty())) {
			if (startHour == null || startHour.isEmpty())
				startHourUpperRange = 0;

			int finishHourSize = finishHour.size();
			Collections.sort(finishHour);
//					if finishHour only contains "00-12", "18-24"
			if (finishHourSize == 2 && finishHour.get(0).equalsIgnoreCase(hour1)
					&& finishHour.get(1).equalsIgnoreCase(hour3)) {
				finishHourLowerRange = 0;
				finishHourUpperRange = 12;
				secondFinishHourLowerRange = 18;
				secondFinishHourUpperRange = 24;

			} else {
				for (int i = 0; i < finishHourSize; i++) {
					if (finishHour.get(i).equalsIgnoreCase(hour1)) {
						finishHourLowerRange = 0;
						finishHourUpperRange = 12;
					} else if (finishHour.get(i).equalsIgnoreCase(hour2)) {
						if (i == 0)
							finishHourLowerRange = 12;
						finishHourUpperRange = 18;
					} else if (finishHour.get(i).equalsIgnoreCase(hour3)) {
						if (i == 0)
							finishHourLowerRange = 18;
						finishHourUpperRange = 24;
					} else {
						throw new BadRequestException("Error: Please use 00-12, 12-18 or 18-00 for hour value");
					}
				}
			}
		}

		Page<Map<String, Object>> eventWrapperPages = eventRepository.search(userAge, companionGender, userId,
				startDateSearch, finishDateSearch, startHourLowerRange, startHourUpperRange, finishHourLowerRange,
				finishHourUpperRange, secondStartHourLowerRange, secondStartHourUpperRange, secondFinishHourLowerRange,
				secondFinishHourUpperRange, creatorMaximumAge, creatorMinimumAge, creatorGenderSearch, eventCity,
				paging);

		List<EventFindAllListDBResponseWrapper> eventAllDBResponse = new ArrayList<>();
		eventWrapperPages.forEach(eventWrap -> {
			EventFindAllListDBResponseWrapper responseWrapper = new EventFindAllListDBResponseWrapper();
			if (StringUtils.isNotEmpty((String) eventWrap.get("status")))
				responseWrapper.setApplicantStatus(ApplicantStatus.valueOf((String) eventWrap.get("status")));
			responseWrapper.setCity((String) eventWrap.get("city"));
			responseWrapper.setCompanionGender(Gender.valueOf((String) eventWrap.get("companion_gender")));
			responseWrapper.setCreatedBy((String) eventWrap.get("created_by"));
			responseWrapper.setCreatorFullName((String) eventWrap.get("full_name"));
			responseWrapper.setCreatorGender(Gender.valueOf((String) eventWrap.get("gender")));
			responseWrapper.setEventId(((BigInteger) eventWrap.get("event_id")).longValue());
			if (((Timestamp) eventWrap.get("finish_date_time")) != null)
				responseWrapper.setFinishDateTime(((Timestamp) eventWrap.get("finish_date_time")).toLocalDateTime());
			responseWrapper.setMaximumAge((Integer) eventWrap.get("maximum_age"));
			responseWrapper.setMinimumAge((Integer) eventWrap.get("minimum_age"));
			responseWrapper.setProfileId(((BigInteger) eventWrap.get("profile_id")).longValue());
			responseWrapper.setStartDateTime(((Timestamp) eventWrap.get("start_date_time")).toLocalDateTime());
			responseWrapper.setTitle((String) eventWrap.get("title"));
			responseWrapper.setCancelled((Boolean) eventWrap.get("cancelled"));

			AtomicReference<String> photoProfileUrl = new AtomicReference<>("");
			profileRepository.findById(((BigInteger) eventWrap.get("profile_id")).longValue())
					.ifPresent(profileCreator -> photoProfileUrl.set(imageFileService.getImageUrl(profileCreator)));

			responseWrapper.setPhotoProfileUrl(photoProfileUrl.get());
			responseWrapper.setHasAcceptedApplicant(!applicantRepository
					.findByEventIdAccepted(((BigInteger) eventWrap.get("event_id")).longValue()).isEmpty());
			eventAllDBResponse.add(responseWrapper);
		});

		return EventFindAllResponseWrapper.builder().pageNumber(pageNumber).pageSize(pageSize)
				.actualSize(eventWrapperPages.getTotalElements()).contentList(eventAllDBResponse).build();
	}
}
