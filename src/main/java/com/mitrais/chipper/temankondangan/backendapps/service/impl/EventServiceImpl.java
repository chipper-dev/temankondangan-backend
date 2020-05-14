package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ApplicantResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
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
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.EventService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

        if (wrapper.getMaximumAge() > 40 || wrapper.getMinimumAge() < 18) {
            throw new BadRequestException("Error: Age must be between 18 and 40!");
        }

        if (wrapper.getMaximumAge() < wrapper.getMinimumAge()) {
            throw new BadRequestException("Error: Inputted age is not valid!");
        }

        // check dateAndTime valid
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
        LocalDateTime startDateAndTime;
        LocalDateTime finishDateAndTime = null;
        startDateAndTime = LocalDateTime.parse(wrapper.getStartDateAndTime(), df);

        if (startDateAndTime.isBefore(LocalDateTime.now().plusDays(1))) {
            throw new BadRequestException("Error: Date inputted have to be after today!");
        }

        if (!StringUtils.isEmpty(wrapper.getFinishDateAndTime())) {

            finishDateAndTime = LocalDateTime.parse(wrapper.getFinishDateAndTime(), df);
            if (startDateAndTime.isAfter(finishDateAndTime)) {
                throw new BadRequestException("Error: Start time must be earlier than finish time!");
            }
        }

        Event event = new Event();
        event.setUser(user);
        event.setTitle(wrapper.getTitle());
        event.setCity(wrapper.getCity());
        event.setStartDateTime(startDateAndTime);
        event.setFinishDateTime(finishDateAndTime);
        event.setCompanionGender(wrapper.getCompanionGender());
        event.setMinimumAge(wrapper.getMinimumAge());
        event.setMaximumAge(wrapper.getMaximumAge());
        event.setAdditionalInfo(wrapper.getAdditionalInfo());
        event.setDataState(DataState.ACTIVE);

        return eventRepository.save(event);

    }

    @Override
    public List<Event> findAll(Integer pageNumber, Integer pageSize, String sortBy, String direction, Long userId) {
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

        Page<Event> pagedResult = eventRepository
                .findAllByMinimumAgeLessThanEqualAndMaximumAgeGreaterThanEqualAndCompanionGenderInAndStartDateTimeAfter(
                        age, age, gender, LocalDateTime.now(), paging);

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

        if (wrapper.getMaximumAge() > 40 || wrapper.getMinimumAge() < 18) {
            throw new BadRequestException("Error: Age must be between 18 and 40!");
        }

        if (wrapper.getMaximumAge() < wrapper.getMinimumAge()) {
            throw new BadRequestException("Error: Inputted age is not valid!");
        }

        // check dateAndTime valid
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
        LocalDateTime startDateAndTime;
        LocalDateTime finishDateAndTime = null;
        startDateAndTime = LocalDateTime.parse(wrapper.getStartDateAndTime(), df);

        if (startDateAndTime.isBefore(LocalDateTime.now().plusDays(1))) {
            throw new BadRequestException("Error: Date inputted have to be after today!");
        }

        if (!StringUtils.isEmpty(wrapper.getFinishDateAndTime())) {

            finishDateAndTime = LocalDateTime.parse(wrapper.getFinishDateAndTime(), df);
            if (startDateAndTime.isAfter(finishDateAndTime)) {
                throw new BadRequestException("Error: Start time must be earlier than finish time!");
            }
        }

        event.setTitle(wrapper.getTitle());
        event.setCity(wrapper.getCity());
        event.setStartDateTime(startDateAndTime);
        event.setFinishDateTime(finishDateAndTime);
        event.setCompanionGender(wrapper.getCompanionGender());
        event.setMinimumAge(wrapper.getMinimumAge());
        event.setMaximumAge(wrapper.getMaximumAge());
        event.setAdditionalInfo(wrapper.getAdditionalInfo());

        return eventRepository.save(event);

    }

    @Override
    public EventDetailResponseWrapper findEventDetail(Long id, Long userId) {
        List<ApplicantResponseWrapper> applicantResponseWrapperList = new ArrayList<>();
        String photoProfileUrl = "";

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        User userCreator = userRepository.findById(event.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", event.getUser().getUserId()));

        Profile profileCreator = profileRepository.findByUserId(userCreator.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", userCreator.getUserId()));

        if(userId.equals(userCreator.getUserId())) {
            applicantRepository.findByEventId(event.getEventId()).forEach(applicant -> {
                Profile profileApplicant = profileRepository.findByUserId(applicant.getApplicantUser().getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", applicant.getApplicantUser().getUserId()));

                applicantResponseWrapperList.add(
                        ApplicantResponseWrapper.builder()
                                .applicantId(applicant.getId())
                                .fullName(profileApplicant.getFullName())
                                .userId(applicant.getApplicantUser().getUserId())
                                .status(applicant.getStatus()).build()
                );
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
        Applicant applicant = applicantRepository.findByApplicantUserIdAndEventId(userApplicantId, eventId).orElseThrow(()-> new ResourceNotFoundException("Applicant", "eventId", eventId));
        Event event = eventRepository.findById(applicant.getEvent().getEventId()).orElseThrow(() -> new ResourceNotFoundException("Event", "id", applicant.getEvent().getEventId()));

        if(isCancelationValid(event.getStartDateTime())) {
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
