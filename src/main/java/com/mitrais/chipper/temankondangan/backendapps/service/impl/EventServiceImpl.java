package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ApplicantResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.EventService;

@Service
public class EventServiceImpl implements EventService {

	private EventRepository eventRepository;
	private UserRepository userRepository;
	private ProfileRepository profileRepository;
	private ApplicantRepository applicantRepository;

	@Value("${app.eventCancelationValidMaxMsec}")
	Long cancelationMax;

	@Autowired
	public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
			ApplicantRepository applicantRepository, ProfileRepository profileRepository) {
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
		this.applicantRepository = applicantRepository;
		this.profileRepository = profileRepository;
	}

	@Override
	public Event create(Long userId, CreateEventWrapper wrapper) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

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
		}

		Event event = new Event();
		event.setUser(user);
		event.setTitle(wrapper.getTitle());
		event.setCity(wrapper.getCity());
		event.setStartDateTime(startDateTime);
		event.setFinishDateTime(finishDateTime);
		event.setCompanionGender(wrapper.getCompanionGender());
		event.setMinimumAge(wrapper.getMinimumAge());
		event.setMaximumAge(wrapper.getMaximumAge());
		event.setAdditionalInfo(wrapper.getAdditionalInfo());
		event.setDataState(DataState.ACTIVE);

		return eventRepository.save(event);

	}

	@Override
	public List<EventFindAllListResponseWrapper> findAll(Integer pageNumber, Integer pageSize, String sortBy,
			String direction, Long userId) {
		Pageable paging;

		if (direction.equalsIgnoreCase("DESC")) {
			paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
		} else if (direction.equalsIgnoreCase("ASC")) {
			paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
		} else {
			throw new BadRequestException("Error: Can only input ASC or DESC for direction!");
		}

		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new BadRequestException("Profile Not found"));
		Integer age = Period.between(profile.getDob(), LocalDate.now()).getYears();
		ArrayList<Gender> gender = new ArrayList<>();
		gender.add(Gender.B);
		gender.add(profile.getGender());

		List<Event> events = eventRepository.findAllByRelevantInfo(age, gender, LocalDateTime.now());

		List<EventFindAllListResponseWrapper> eventAllResponse = new ArrayList<EventFindAllListResponseWrapper>();
		for (Event event : events) {
			User userCreator = event.getUser();
			Profile profileCreator = profileRepository.findByUserId(userCreator.getUserId())
					.orElseThrow(() -> new ResourceNotFoundException("Profile", "id", userCreator.getUserId()));
			String photoProfileUrl = "";

			if (profileCreator.getPhotoProfile() != null) {
				photoProfileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/imagefile/download/")
						.path(String.valueOf(profileCreator.getProfileId())).toUriString();
			}

			EventFindAllListResponseWrapper e = EventFindAllListResponseWrapper.builder().eventId(event.getEventId())
					.creatorFullName(profileCreator.getFullName()).createdBy(event.getCreatedBy())
					.photoProfileUrl(photoProfileUrl).title(event.getTitle()).city(event.getCity())
					.startDateTime(event.getStartDateTime()).finishDateTime(event.getFinishDateTime())
					.minimumAge(event.getMinimumAge()).maximumAge(event.getMaximumAge())
					.creatorGender(profileCreator.getGender()).companionGender(event.getCompanionGender()).build();

			eventAllResponse.add(e);
		}

		int start = (int) paging.getOffset();
		int end = (start + paging.getPageSize()) > eventAllResponse.size() ? eventAllResponse.size()
				: (start + paging.getPageSize());
		Page<EventFindAllListResponseWrapper> pagedResult = new PageImpl<EventFindAllListResponseWrapper>(
				eventAllResponse.subList(start, end), paging, eventAllResponse.size());

		if (pagedResult.hasContent()) {
			return pagedResult.getContent();
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public Event edit(Long userId, EditEventWrapper wrapper) {
		Event event = eventRepository.findById(wrapper.getEventId())
				.orElseThrow(() -> new ResourceNotFoundException("Event", "id", wrapper.getEventId()));

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
		}

		event.setTitle(wrapper.getTitle());
		event.setCity(wrapper.getCity());
		event.setStartDateTime(startDateTime);
		event.setFinishDateTime(finishDateTime);
		event.setCompanionGender(wrapper.getCompanionGender());
		event.setMinimumAge(wrapper.getMinimumAge());
		event.setMaximumAge(wrapper.getMaximumAge());
		event.setAdditionalInfo(wrapper.getAdditionalInfo());

		return eventRepository.save(event);

	}

	@Override
	public EventDetailResponseWrapper findEventDetail(String eventIdStr, Long userId) {
		List<ApplicantResponseWrapper> applicantResponseWrapperList = new ArrayList<>();
		String photoProfileUrl = "";
		Long id;

		// Custo exception as requested by Tester, when input param.
		try {
			  id = Long.parseLong(eventIdStr);
		} catch (NumberFormatException ex) {
			throw new BadRequestException("Error: Cannot use the text value as parameter, please use the number format value!");
		}

		Event event = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

		User userCreator = userRepository.findById(event.getUser().getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", event.getUser().getUserId()));

		Profile profileCreator = profileRepository.findByUserId(userCreator.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Profile", "id", userCreator.getUserId()));

		if (userId.equals(userCreator.getUserId())) {
			applicantRepository.findByEventId(event.getEventId()).forEach(applicant -> {
				Profile profileApplicant = profileRepository.findByUserId(applicant.getApplicantUser().getUserId())
						.orElseThrow(() -> new ResourceNotFoundException("Profile", "id",
								applicant.getApplicantUser().getUserId()));

				applicantResponseWrapperList.add(ApplicantResponseWrapper.builder().applicantId(applicant.getId())
						.fullName(profileApplicant.getFullName()).userId(applicant.getApplicantUser().getUserId())
						.status(applicant.getStatus()).build());
			});
		}

		if (profileCreator.getPhotoProfile() != null) {
			photoProfileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/imagefile/download/")
					.path(String.valueOf(profileCreator.getProfileId())).toUriString();
		}

		return EventDetailResponseWrapper.builder().eventId(event.getEventId()).creatorUserId(userCreator.getUserId())
				.photoProfileUrl(photoProfileUrl).title(event.getTitle()).city(event.getCity())
				.dateAndTime(event.getStartDateTime()).minimumAge(event.getMinimumAge())
				.maximumAge(event.getMaximumAge()).companionGender(event.getCompanionGender())
				.additionalInfo(event.getAdditionalInfo()).applicantList(applicantResponseWrapperList)
				.isCreator(userId.equals(userCreator.getUserId())).build();
	}

	@Override
	public void apply(Long userId, Long eventId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

		if (user.getUserId().equals(event.getUser().getUserId())) {
			throw new BadRequestException("Error: You cannot apply to your own event!");
		}

		if (applicantRepository.existsByApplicantUserAndEvent(user, event)) {
			throw new BadRequestException("Error: You have applied to this event");
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
				.orElseThrow(() -> new ResourceNotFoundException("Applicant", "eventId", eventId));
		Event event = eventRepository.findById(applicant.getEvent().getEventId())
				.orElseThrow(() -> new ResourceNotFoundException("Event", "id", applicant.getEvent().getEventId()));

		if (isCancelationValid(event.getStartDateTime())) {
			applicantRepository.delete(applicant);
		} else {
			throw new BadRequestException("Error: The event will be started less than 48 hours");
		}

	}

	private boolean isCancelationValid(LocalDateTime eventDate) {
		Duration duration = Duration.between(LocalDateTime.now(), eventDate);
		System.out.println(duration.getSeconds() * 1000);

		return duration.getSeconds() * 1000 > cancelationMax;
	}
}
