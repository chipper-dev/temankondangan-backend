package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
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

import com.google.firebase.messaging.FirebaseMessagingException;
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
import com.mitrais.chipper.temankondangan.backendapps.service.NotificationService;
import com.mitrais.chipper.temankondangan.backendapps.service.RatingService;

@Service
public class EventServiceImpl implements EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    private static final String ERROR_SORT_DIRECTION = "Error: Can only input ASC or DESC for direction!";
    private static final String ERROR_EVENT_START_IN_24HOURS = "Error: The event will be started in less than 24 hours";
    private static final String ERROR_SORTBY = "Error: Can only input createdDate or startDateTime for sortBy!";

    Predicate<String> sortByCreatedDate = s -> s.equals("createdDate");
    Predicate<String> sortByStartDateTime = s -> s.equals("startDateTime");
    Predicate<String> sortByLatestApplied = s -> s.equals("latestApplied");

    enum NotificationType {
        APPLY_EVENT, CANCEL_APPLY_EVENT, EDIT_EVENT, CANCEL_EVENT
    }

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private ProfileRepository profileRepository;
    private ApplicantRepository applicantRepository;
    private ImageFileService imageFileService;
    private NotificationService notificationService;
    private RatingService ratingService;
    private AuditReader auditReader;

    @Value("${app.eventCancelationValidMaxMsec}")
    Long cancelationMax;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                            ApplicantRepository applicantRepository, ProfileRepository profileRepository,
                            ImageFileService imageFileService, NotificationService notificationService, RatingService ratingService,
                            AuditReader auditReader) {

        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.applicantRepository = applicantRepository;
        this.profileRepository = profileRepository;
        this.imageFileService = imageFileService;
        this.notificationService = notificationService;
        this.ratingService = ratingService;
        this.auditReader = auditReader;
    }

    @Override
    public Event create(Long userId, CreateEventWrapper wrapper) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

        checkValidAge(wrapper.getMinimumAge(), wrapper.getMaximumAge());

        // check dateAndTime valid
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
        LocalDateTime startDateTime;
        LocalDateTime finishDateTime = null;
        startDateTime = LocalDateTime.parse(wrapper.getStartDateTime(), df);

        if (startDateTime.isBefore(LocalDateTime.now().plusDays(1))) {
            throw new BadRequestException("Error: Date inputted have to be after today!");
        }

        if (StringUtils.isNotEmpty(wrapper.getFinishDateTime())) {

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

        Profile profile = profileRepository.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException(Entity.PROFILE.getLabel(), Entity.USER_ID.getLabel(), userId));
        Integer age = Period.between(profile.getDob(), LocalDate.now()).getYears();
        ArrayList<Gender> gender = new ArrayList<>();
        gender.add(Gender.B);
        gender.add(profile.getGender());

        if (!(sortByCreatedDate.test(sortBy) || sortByStartDateTime.test(sortBy))) {
            throw new BadRequestException(ERROR_SORTBY);
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
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(Entity.USER.getLabel(), Entity.USER_ID.getLabel(), userId));

        if (!(sortByCreatedDate.test(sortBy) || sortByStartDateTime.test(sortBy))) {
            throw new BadRequestException(ERROR_SORTBY);
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
            throw new BadRequestException(ERROR_EVENT_START_IN_24HOURS);
        }

        if (!event.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Error: Users are not authorized to edit this event");
        }

        checkValidAge(wrapper.getMinimumAge(), wrapper.getMaximumAge());

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
        Event eventUpdated = eventRepository.save(event);

        List<String> fieldsUpdated = findFieldsUpdated(eventUpdated);
        System.out.println("Updated Fields: " + fieldsUpdated);

        sendMultipleNotification(NotificationType.EDIT_EVENT, event, fieldsUpdated);

        return eventUpdated;
    }

    @Override
    public EventDetailResponseWrapper findEventDetail(String eventIdStr, Long userId) {
        List<ApplicantResponseWrapper> applicantResponseWrapperList = new ArrayList<>();
        boolean isApplied = false;
        boolean isCreatorRated = false;
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

                boolean isApplicantRated = ratingService.isRated(userId, event.getEventId());

                if (applicant.getStatus().equals(ApplicantStatus.ACCEPTED)) {
                    acceptedApplicant.setUserId(profileApplicant.getUser().getUserId());
                    acceptedApplicant.setFullName(profileApplicant.getFullName());
                    acceptedApplicant.setGender(profileApplicant.getGender());
                    acceptedApplicant.setPhotoProfileUrl(imageFileService.getImageUrl(profileApplicant));
                    acceptedApplicant.setRated(isApplicantRated);
                    applicantResponseWrapperList.add(0,
                            ApplicantResponseWrapper.builder().applicantId(applicant.getId())
                                    .fullName(profileApplicant.getFullName())
                                    .userId(applicant.getApplicantUser().getUserId()).status(applicant.getStatus())
                                    .isRated(isApplicantRated).build());

                } else {
                    applicantResponseWrapperList.add(ApplicantResponseWrapper.builder().applicantId(applicant.getId())
                            .fullName(profileApplicant.getFullName()).userId(applicant.getApplicantUser().getUserId())
                            .status(applicant.getStatus()).isRated(isApplicantRated).build());

                }
            }));
        } else {
            User userApplicant = userRepository.findById(userId).orElseThrow(
                    () -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", event.getUser().getUserId()));
            isApplied = applicantRepository.existsByApplicantUserAndEvent(userApplicant, event);
            isCreatorRated = ratingService.isRated(userId, event.getEventId());
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
                .acceptedApplicant(acceptedApplicant).cancelled(event.getCancelled()).isRated(isCreatorRated)
                .createdDateTime(LocalDateTime.ofInstant(event.getCreatedDate().toInstant(), ZoneId.systemDefault()))
                .build();
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

        if (Boolean.TRUE.equals(event.getCancelled())) {
            throw new BadRequestException("Error: You cannot applied to cancelled event");
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

        sendSingleNotification(NotificationType.APPLY_EVENT, applicant.getEvent(), profile.getFullName());
    }

    @Override
    public void cancelEvent(Long userApplicantId, Long eventId) {
        Applicant applicant = applicantRepository.findByApplicantUserIdAndEventId(userApplicantId, eventId).orElseThrow(
                () -> new ResourceNotFoundException(Entity.APPLICANT.getLabel(), Entity.EVENT_ID.getLabel(), eventId));
        Event event = eventRepository.findById(applicant.getEvent().getEventId()).orElseThrow(
                () -> new ResourceNotFoundException(Entity.EVENT.getLabel(), "id", applicant.getEvent().getEventId()));

        if (applicant.getStatus().equals(ApplicantStatus.REJECTED)) {
            throw new BadRequestException("Error: You are already rejected. You don't need to cancel it anymore.");
        }

        if (Boolean.TRUE.equals(event.getCancelled())) {
            throw new BadRequestException("Error: You cannot cancel to cancelled event");
        }

        if (isCancelationValid(event.getStartDateTime())) {
            applicantRepository.delete(applicant);
        } else {
            throw new BadRequestException(ERROR_EVENT_START_IN_24HOURS);
        }

        Profile profile = profileRepository.findByUserId(applicant.getApplicantUser().getUserId()).orElse(null);
        String name = profile == null ? "Someone" : profile.getFullName();
        sendSingleNotification(NotificationType.CANCEL_APPLY_EVENT, applicant.getEvent(), name);
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

        if (LocalDateTime.now().isAfter(event.getStartDateTime())) {
            throw new BadRequestException("Error: Past event cannot be canceled");
        }

        if (isCancelationValid(event.getStartDateTime())) {
            event.setCancelled(true);
            eventRepository.save(event);

            sendMultipleNotification(NotificationType.CANCEL_EVENT, event, null);
        } else {
            throw new BadRequestException(ERROR_EVENT_START_IN_24HOURS);
        }
    }

    @Override
    public List<AppliedEventWrapper> findActiveAppliedEvent(Long userId, String sortBy, String direction,
                                                            String applicantStatusStr) {
        List<AppliedEventWrapper> resultList = new ArrayList<>();
        boolean allStatus = true;
        if (!(sortByCreatedDate.test(sortBy) || sortByStartDateTime.test(sortBy) || sortByLatestApplied.test(sortBy))) {
            throw new BadRequestException(
                    "Error: Can only input createdDate, startDateTime or latestApplied for sortBy!");
        } else if (sortByLatestApplied.test(sortBy)) {
            sortBy = "a.createdDate";
        }

        ApplicantStatus applicantStatus;
        if (EnumUtils.isValidEnum(ApplicantStatus.class, applicantStatusStr)) {
            applicantStatus = ApplicantStatus.valueOf(applicantStatusStr);
            if (!applicantStatus.equals(ApplicantStatus.ALLSTATUS))
                allStatus = false;
        } else {
            throw new BadRequestException("Error: Please input a valid applicant status");
        }

        Sort sort;
        if (direction.equalsIgnoreCase("DESC")) {
            sort = Sort.by(sortBy).descending();
        } else if (direction.equalsIgnoreCase("ASC")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            throw new BadRequestException(ERROR_SORT_DIRECTION);
        }

        eventRepository.findActiveAppliedEvent(userId, applicantStatus, allStatus, sort).forEach(event -> {
            AppliedEventWrapper wrapper = new AppliedEventWrapper();
            wrapper.setEventId(event.getEventId());
            wrapper.setTitle(event.getTitle());
            wrapper.setCity(event.getCity());
            wrapper.setStartDateTime(event.getStartDateTime());
            wrapper.setFinishDateTime(event.getFinishDateTime());
            wrapper.setCancelled(event.getCancelled());
            wrapper.setCreatedDateTime(
                    LocalDateTime.ofInstant(event.getCreatedDate().toInstant(), ZoneId.systemDefault()));

            profileRepository.findByUserId(event.getUser().getUserId()).ifPresent(profile -> {
                wrapper.setPhotoProfileUrl(imageFileService.getImageUrl(profile));
                wrapper.setCreatorFullName(profile.getFullName());
                wrapper.setCreatorGender(profile.getGender());
            });

            applicantRepository.findByApplicantUserIdAndEventId(userId, event.getEventId()).ifPresent(applicant -> {
                wrapper.setApplicantStatus(applicant.getStatus());
                wrapper.setAppliedDateTime(
                        LocalDateTime.ofInstant(applicant.getCreatedDate().toInstant(), ZoneId.systemDefault()));
            });

            resultList.add(wrapper);
        });

        return resultList;
    }

    @Override
    public List<AppliedEventWrapper> findPastAppliedEvent(Long userId, String sortBy, String direction,
                                                          String applicantStatusStr) {
        List<AppliedEventWrapper> resultList = new ArrayList<>();
        if (!(sortByCreatedDate.test(sortBy) || sortByStartDateTime.test(sortBy) || sortByLatestApplied.test(sortBy))) {
            throw new BadRequestException(
                    "Error: Can only input createdDate, startDateTime or latestApplied for sortBy!");
        } else if (sortByLatestApplied.test(sortBy)) {
            sortBy = "a.createdDate";
        }

        ApplicantStatus applicantStatus = ApplicantStatus.APPLIED;
        // value for ALL STATUS
        boolean allStatus = true;
        boolean pastTimeOnly = true;
        List<Boolean> isCancelled = new ArrayList<>();

        if (EnumUtils.isValidEnum(ApplicantStatus.class, applicantStatusStr)) {
            applicantStatus = ApplicantStatus.valueOf(applicantStatusStr);
//			if any applicant status besides ALLSTATUS
            if (applicantStatus.equals(ApplicantStatus.ALLSTATUS)) {
                isCancelled.add(true);
                isCancelled.add(false);
            } else {
                allStatus = false;
                isCancelled.add(false);
            }
        } else if (applicantStatusStr.equals("CANCELED")) {
            isCancelled.add(true);
            pastTimeOnly = false;
        } else {
            throw new BadRequestException("Error: Please input a valid applicant status");
        }

        Sort sort;
        if (direction.equalsIgnoreCase("DESC")) {
            sort = Sort.by(sortBy).descending();
        } else if (direction.equalsIgnoreCase("ASC")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            throw new BadRequestException(ERROR_SORT_DIRECTION);
        }

        eventRepository.findPastAppliedEvent(userId, applicantStatus, allStatus, pastTimeOnly, isCancelled, sort)
                .forEach(event -> {
                    logger.info(event.toString());

                    AppliedEventWrapper wrapper = new AppliedEventWrapper();
                    wrapper.setEventId(event.getEventId());
                    wrapper.setTitle(event.getTitle());
                    wrapper.setCity(event.getCity());
                    wrapper.setStartDateTime(event.getStartDateTime());
                    wrapper.setFinishDateTime(event.getFinishDateTime());
                    wrapper.setCancelled(event.getCancelled());
                    wrapper.setCreatedDateTime(
                            LocalDateTime.ofInstant(event.getCreatedDate().toInstant(), ZoneId.systemDefault()));

                    profileRepository.findByUserId(event.getUser().getUserId()).ifPresent(profile -> {
                        wrapper.setPhotoProfileUrl(imageFileService.getImageUrl(profile));
                        wrapper.setCreatorFullName(profile.getFullName());
                        wrapper.setCreatorGender(profile.getGender());
                    });

                    applicantRepository.findByApplicantUserIdAndEventId(userId, event.getEventId())
                            .ifPresent(applicant -> {
                                wrapper.setApplicantStatus(applicant.getStatus());
                                wrapper.setAppliedDateTime(LocalDateTime
                                        .ofInstant(applicant.getCreatedDate().toInstant(), ZoneId.systemDefault()));
                            });

                    resultList.add(wrapper);
                });

        return resultList;
    }

    @Override
    public EventFindAllResponseWrapper search(Long userId, Integer pageNumber, Integer pageSize, String sortBy,
                                              String direction, String creatorGender, Integer creatorMaximumAge, Integer creatorMinimumAge,
                                              String startDate, String finishDate, List<String> startHour, List<String> finishHour, List<String> city,
                                              Double zoneOffset) {
        // check sortBy and direction
        if (!(sortByCreatedDate.test(sortBy) || sortByStartDateTime.test(sortBy))) {
            throw new BadRequestException(ERROR_SORTBY);
        } else if (sortByStartDateTime.test(sortBy)) {
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
        checkValidAge(creatorMinimumAge, creatorMaximumAge);

        List<String> zoneOffset45 = Arrays.asList("12.75", "8.75", "5.75");
        List<String> zoneOffset30 = Arrays.asList("-9.5", "-3.5", "3.5", "4.5", "5.5", "6.5", "9.5", "10.5");
        if ((!zoneOffset45.contains(zoneOffset.toString()) || zoneOffset % 0.25 != 0)
                && (!zoneOffset30.contains(zoneOffset.toString()) || zoneOffset % 0.5 != 0)
                && (zoneOffset < -12 || zoneOffset > 14 || zoneOffset % 1 != 0)) {
            throw new BadRequestException("Error: Please input a valid zone offset");
        }

        // check user profile who is searching
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Entity.PROFILE.getLabel(), "userId", userId));
        Integer userAge = Period.between(profile.getDob(), LocalDate.now()).getYears();
        List<String> companionGender = Arrays.asList("B", profile.getGender().toString());

        // check inputted gender in search
        List<String> creatorGenderSearch = new ArrayList<>();

        if (creatorGender.equalsIgnoreCase("B")) {
            creatorGenderSearch.add("L");
            creatorGenderSearch.add("P");
        } else if (creatorGender.equalsIgnoreCase("L") || creatorGender.equalsIgnoreCase("P")) {
            creatorGenderSearch.add(creatorGender);
        } else {
            throw new BadRequestException("Error: Can only input L, P or B for creatorGender!");
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

        long zoneOffsetInMinutes = (long) (zoneOffset * 60L);
        LocalDateTime currentDateTime = LocalDateTime.now().minusMinutes(zoneOffsetInMinutes);
        DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
        LocalDateTime startDateSearch = currentDateTime;
        LocalDateTime finishDateSearch = LocalDate.now().plusDays(90).atTime(LocalTime.MAX)
                .minusMinutes(zoneOffsetInMinutes);

        if ((StringUtils.isNotEmpty(startDate))) {
            startDateSearch = LocalDate.parse(startDate, dfDate).atTime(LocalTime.now())
                    .minusMinutes(zoneOffsetInMinutes);
            if (startDateSearch.toLocalDate().equals(currentDateTime.toLocalDate())) {
                startDateSearch = currentDateTime;
            } else {
                startDateSearch = LocalDate.parse(startDate, dfDate).atStartOfDay().minusMinutes(zoneOffsetInMinutes);
            }
            finishDateSearch = LocalDate.parse(finishDate, dfDate).atTime(LocalTime.MAX)
                    .minusMinutes(zoneOffsetInMinutes);

            if (startDateSearch.isBefore(currentDateTime)) {
                throw new BadRequestException("Error: Date inputted have to be today or after!");
            }

            if (startDateSearch.isAfter(finishDateSearch)) {
                throw new BadRequestException("Error: startDate must be earlier than finishDate!");
            }
        }

        String hour1 = "00-12";
        String hour2 = "12-18";
        String hour3 = "18-00";

        LocalTime startHourLowerRange = LocalTime.MIN;
        LocalTime startHourUpperRange = LocalTime.MAX;
        LocalTime finishHourLowerRange = LocalTime.MIN;
        LocalTime finishHourUpperRange = LocalTime.MIN;

        LocalTime secondStartHourLowerRange = LocalTime.MIN;
        LocalTime secondStartHourUpperRange = LocalTime.MIN;
        LocalTime secondFinishHourLowerRange = LocalTime.MIN;
        LocalTime secondFinishHourUpperRange = LocalTime.MIN;

        IntFunction<LocalTime> intToLocalTime = x -> {
            if (x - zoneOffset == 24) {
                return LocalTime.MAX;
            }
            return LocalTime.of(0, 0, 0).plusMinutes((x * 60) - zoneOffsetInMinutes);
        };

        // check starthour
        if (!(startHour == null || startHour.isEmpty())) {
            int startHourSize = startHour.size();
            Collections.sort(startHour);

            for (int i = 0; i < startHourSize; i++) {
                if (startHour.get(i).equalsIgnoreCase(hour1)) {
                    startHourLowerRange = intToLocalTime.apply(0);
                    startHourUpperRange = intToLocalTime.apply(12);
                } else if (startHour.get(i).equalsIgnoreCase(hour2)) {
                    if (i == 0) {
                        startHourLowerRange = intToLocalTime.apply(12);
                    }
                    startHourUpperRange = intToLocalTime.apply(18);
                } else if (startHour.get(i).equalsIgnoreCase(hour3)) {
                    if (i == 0) {
                        startHourLowerRange = intToLocalTime.apply(18);
                    }
                    startHourUpperRange = intToLocalTime.apply(24);
                } else {
                    throw new BadRequestException("Error: Please use 00-12, 12-18 or 18-00 for hour value");
                }
            }

            if (startHourSize == 3) {
                secondStartHourLowerRange = LocalTime.MIN;
                secondStartHourUpperRange = LocalTime.MIN;
                startHourLowerRange = LocalTime.MIN;
                startHourUpperRange = LocalTime.MAX;
            } else if (startHourSize == 2 && startHour.get(0).equalsIgnoreCase(hour1)
                    && startHour.get(1).equalsIgnoreCase(hour3)) {
                startHourUpperRange = startHourUpperRange.plusHours(12);
                startHourLowerRange = startHourLowerRange.plusHours(18);
            }

            if (startHourLowerRange.isAfter(startHourUpperRange)) {
                secondStartHourLowerRange = startHourLowerRange;
                secondStartHourUpperRange = LocalTime.MAX;
                startHourLowerRange = LocalTime.MIN;
            }
        }

        // check finishhour
        if (!(finishHour == null || finishHour.isEmpty())) {
            if (startHour == null || startHour.isEmpty()) {
                startHourUpperRange = LocalTime.MIN;
            }
            int finishHourSize = finishHour.size();
            Collections.sort(finishHour);

            for (int i = 0; i < finishHourSize; i++) {
                if (finishHour.get(i).equalsIgnoreCase(hour1)) {
                    finishHourLowerRange = intToLocalTime.apply(0);
                    finishHourUpperRange = intToLocalTime.apply(12);
                } else if (finishHour.get(i).equalsIgnoreCase(hour2)) {
                    if (i == 0) {
                        finishHourLowerRange = intToLocalTime.apply(12);
                    }
                    finishHourUpperRange = intToLocalTime.apply(18);
                } else if (finishHour.get(i).equalsIgnoreCase(hour3)) {
                    if (i == 0) {
                        finishHourLowerRange = intToLocalTime.apply(18);
                    }
                    finishHourUpperRange = intToLocalTime.apply(24);
                } else {
                    throw new BadRequestException("Error: Please use 00-12, 12-18 or 18-00 for hour value");
                }
            }

            if (finishHourSize == 3) {
                secondFinishHourLowerRange = LocalTime.MIN;
                secondFinishHourUpperRange = LocalTime.MIN;
                finishHourLowerRange = LocalTime.MIN;
                finishHourUpperRange = LocalTime.MAX;
            } else if (finishHourSize == 2 && finishHour.get(0).equalsIgnoreCase(hour1)
                    && finishHour.get(1).equalsIgnoreCase(hour3)) {
                finishHourUpperRange = finishHourUpperRange.plusHours(12);
                finishHourLowerRange = finishHourLowerRange.plusHours(18);
            }

            if (finishHourLowerRange.isAfter(finishHourUpperRange)) {
                secondFinishHourLowerRange = finishHourLowerRange;
                secondFinishHourUpperRange = LocalTime.MAX;
                finishHourLowerRange = LocalTime.MIN;
            }
        }

        // used to disable search between 00:00 and 00:00,
        // sometimes there are event that don't have finishDateTime
        // and the startDateTime is at 00:00
        if (startHourLowerRange.equals(LocalTime.MIN) && startHourUpperRange.equals(LocalTime.MIN)) {
            startHourLowerRange = LocalTime.NOON;
        }
        if (finishHourLowerRange.equals(LocalTime.MIN) && finishHourUpperRange.equals(LocalTime.MIN)) {
            finishHourLowerRange = LocalTime.NOON;
        }
        if (secondStartHourLowerRange.equals(LocalTime.MIN) && secondStartHourUpperRange.equals(LocalTime.MIN)) {
            secondStartHourLowerRange = LocalTime.NOON;
        }
        if (secondFinishHourLowerRange.equals(LocalTime.MIN) && secondFinishHourUpperRange.equals(LocalTime.MIN)) {
            secondFinishHourLowerRange = LocalTime.NOON;
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
            responseWrapper.setCreatedDateTime(LocalDateTime
                    .ofInstant(((Date) eventWrap.get("created_date")).toInstant(), ZoneId.systemDefault()));

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

    private void checkValidAge(Integer minimumAge, Integer maximumAge) {

        if (minimumAge < 18) {
            throw new BadRequestException("Error: Minimum age must be 18!");
        }

        if (maximumAge < minimumAge) {
            throw new BadRequestException("Error: Inputted age is not valid!");
        }
    }

    private boolean isCancelationValid(LocalDateTime eventDate) {
        Duration duration = Duration.between(LocalDateTime.now(), eventDate);

        return duration.getSeconds() * 1000 > cancelationMax;
    }

    private void sendSingleNotification(NotificationType notificationType, Event event, String name) {
        Map<String, String> data = new HashMap<>();
        data.put(Entity.EVENT_ID.getLabel(), event.getEventId().toString());
        data.put("isMyEvent", Boolean.TRUE.toString());

        String title = tittleNotificationMsg(notificationType);
        String body = bodyNotificationMsg(notificationType, name, event.getTitle());

        try {
            notificationService.send(title, body, event.getUser(), data);
        } catch (FirebaseMessagingException e) {
            logger.error("FirebaseMessagingException", e);
        }
    }

    private void sendMultipleNotification(NotificationType notificationType, Event event, List<String> fields) {
        List<Applicant> applicantList = applicantRepository.findByEventIdAcceptedAndApplied(event.getEventId());
        List<User> userList = applicantList.stream().map(Applicant::getApplicantUser).collect(Collectors.toList());

        Profile profile = profileRepository.findByUserId(event.getUser().getUserId()).orElse(null);
        String name = profile == null ? "Someone" : profile.getFullName();

        String title = tittleNotificationMsg(notificationType);
        String body = bodyNotificationMsg(notificationType, name, event.getTitle());
        if (fields != null && !fields.isEmpty()) {
            if (fields.contains("Title")) {
                String previousTitle = getPreviousEvent(event).getTitle();
                body = bodyNotificationMsg(notificationType, name,
                        String.join(", ", fields).concat(" of the ").concat(event.getTitle()).concat(" (previously "))
                        .concat(previousTitle)
                        .concat(")");
            } else {
                body = bodyNotificationMsg(notificationType, name,
                        String.join(", ", fields).concat(" of the ").concat(event.getTitle()));
            }
        }
        logger.info("Body of Notification Message: {}", body);

        Map<String, String> data = new HashMap<>();
        data.put("eventId", event.getEventId().toString());
        data.put("isMyEvent", Boolean.FALSE.toString());

        try {
            notificationService.sendMultiple(title, body, userList, data);
        } catch (FirebaseMessagingException e) {
            logger.error("FirebaseMessagingException", e);
        }
    }

    private String tittleNotificationMsg(NotificationType notificationType) {
        switch (notificationType) {
            case APPLY_EVENT:
                return "Someone applied to your event";
            case CANCEL_APPLY_EVENT:
                return "Someone cancel application to your event";
            case EDIT_EVENT:
                return "The Event info that you have applied was edited";
            case CANCEL_EVENT:
                return "The Event that you have applied was canceled";
            default:
                return "";
        }
    }

    private String bodyNotificationMsg(NotificationType notificationType, String name, String tittleEvent) {
        switch (notificationType) {
            case APPLY_EVENT:
                return name + " apply to " + tittleEvent;
            case CANCEL_APPLY_EVENT:
                return name + " cancel application to " + tittleEvent;
            case EDIT_EVENT:
                return name + " edit the " + tittleEvent;
            case CANCEL_EVENT:
                return name + " cancel " + tittleEvent;
            default:
                return "";
        }
    }

    // To retrieve the updated fields
    private List<String> findFieldsUpdated(Event eventResult) {
        List<String> fieldListResult = new ArrayList<>();
        Number revisionNumber = auditReader.getRevisionNumberForDate(new Date());
        Field[] fields = eventResult.getClass().getDeclaredFields(); // Get properties of the Event class

        Arrays.stream(fields).map(Field::getName)
                .filter(name -> !name.equalsIgnoreCase("eventId") && !name.equalsIgnoreCase("user")).forEach(name -> {
            final Long hits = (Long) auditReader.createQuery().forRevisionsOfEntity(Event.class, false, false)
                    .add(AuditEntity.id().eq(eventResult.getEventId()))
                    .add(AuditEntity.revisionNumber().eq(revisionNumber))
                    .add(AuditEntity.property(name).hasChanged()).addProjection(AuditEntity.id().count())
                    .getSingleResult();

            if (hits == 1) {
                // propertyName changed at revisionNumber
                fieldListResult.add(splitCamelCase(name));
            }
        });

        return fieldListResult;
    }

    private Event getPreviousEvent(Event event) {
        List<Number> revNumbers = auditReader.getRevisions(Event.class, event.getEventId());
        return auditReader.find(Event.class, event.getEventId(), revNumbers.get(revNumbers.size() - 2));
    }

    private String splitCamelCase(String s) {
        String splitedCamelCase = s.replaceAll(String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])"), " ");

        return StringUtils.capitalize(splitedCamelCase);
    }

}
