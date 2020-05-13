package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ApplicantResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
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

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, ApplicantRepository applicantRepository, ProfileRepository profileRepository) {
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
        LocalDateTime dateAndTime;
        dateAndTime = LocalDateTime.parse(wrapper.getDateAndTime(), df);

        if (dateAndTime.isBefore(LocalDateTime.now().plusDays(1))) {
            throw new BadRequestException("Error: Date inputted have to be after today!");
        }

        Event event = new Event();
        event.setUser(user);
        event.setTitle(wrapper.getTitle());
        event.setCity(wrapper.getCity());
        event.setDateAndTime(dateAndTime);
        event.setCompanionGender(wrapper.getCompanionGender());
        event.setMinimumAge(wrapper.getMinimumAge());
        event.setMaximumAge(wrapper.getMaximumAge());
        event.setAdditionalInfo(wrapper.getAdditionalInfo());
        event.setDataState(DataState.ACTIVE);

        return eventRepository.save(event);

    }

    @Override
    public List<Event> findAll(Integer pageNumber, Integer pageSize, String sortBy, String direction) {
        Pageable paging;

        if (direction.equalsIgnoreCase("DESC")) {
            paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
        } else if (direction.equalsIgnoreCase("ASC")) {
            paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        } else {
            throw new BadRequestException("Error: Can only input ASC or DESC for direction!");
        }

        Page<Event> pagedResult = eventRepository.findAll(paging);

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
        LocalDateTime dateAndTime;
        dateAndTime = LocalDateTime.parse(wrapper.getDateAndTime(), df);

        if (dateAndTime.isBefore(LocalDateTime.now().plusDays(1))) {
            throw new BadRequestException("Error: Date inputted have to be after today!");
        }

        event.setTitle(wrapper.getTitle());
        event.setCity(wrapper.getCity());
        event.setDateAndTime(dateAndTime);
        event.setCompanionGender(wrapper.getCompanionGender());
        event.setMinimumAge(wrapper.getMinimumAge());
        event.setMaximumAge(wrapper.getMaximumAge());
        event.setAdditionalInfo(wrapper.getAdditionalInfo());

        return eventRepository.save(event);

    }

    @Override
    public EventDetailResponseWrapper findById(Long id) {
        List<ApplicantResponseWrapper> applicantResponseWrapperList = new ArrayList<>();
        String photoProfileUrl = "";

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        User userCreator = userRepository.findById(event.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", event.getUser().getUserId()));

        Profile profileCreator = profileRepository.findByUserId(userCreator.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", userCreator.getUserId()));


        applicantRepository.findByEventId(event.getEventId()).forEach(applicant -> {
            Profile profileApplicant = profileRepository.findByUserId(applicant.getApplicantUser().getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", applicant.getApplicantUser().getUserId()));

            applicantResponseWrapperList.add(
                    ApplicantResponseWrapper.builder()
                            .fullName(profileApplicant.getFullName())
                            .userId(applicant.getApplicantUser().getUserId())
                            .status(applicant.getStatus()).build()
            );
        });

        if (profileCreator.getPhotoProfile() != null) {
            photoProfileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/imagefile/download/")
                    .path(String.valueOf(profileCreator.getProfileId())).toUriString();
        }

        return EventDetailResponseWrapper.builder()
                .eventId(event.getEventId())
                .creatorUserId(userCreator.getUserId())
                .photoProfileUrl(photoProfileUrl)
                .title(event.getTitle())
                .city(event.getCity())
                .dateAndTime(event.getDateAndTime())
                .minimumAge(event.getMinimumAge())
                .maximumAge(event.getMaximumAge())
                .companionGender(event.getCompanionGender())
                .additionalInfo(event.getAdditionalInfo())
                .applicantList(applicantResponseWrapperList)
                .build();
    }


}
