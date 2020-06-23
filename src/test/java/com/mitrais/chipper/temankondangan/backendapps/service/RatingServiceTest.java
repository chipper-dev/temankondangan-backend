package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.RatingRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RatingServiceTest {
    @Mock
    RatingRepository ratingRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    ApplicantRepository applicantRepository;
    @Mock
    ProfileRepository profileRepository;

    @InjectMocks
    RatingServiceImpl ratingService;

    @Test
    public void sendApplicantRatingSuccess() throws Exception {
        User userCreator = User.builder().userId(2L).email("ini@mail.com").build();
        User userApplicant = User.builder().userId(1L).email("ini@mail.com").build();
        Event event = Event.builder().user(userCreator).title("Lorem Ipsum").city("Jakarta").cancelled(false).build();

        List<Applicant> applicantList = Arrays.asList(
                Applicant.builder().applicantUser(userApplicant).status(ApplicantStatus.ACCEPTED).event(event).build()
        );

        Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        Mockito.when(applicantRepository.findByEventIdAccepted(anyLong())).thenReturn(applicantList);

        RatingServiceImpl ratingServiceSpy = PowerMockito.spy(new RatingServiceImpl(ratingRepository, eventRepository, applicantRepository, profileRepository));
        PowerMockito.doNothing().when(ratingServiceSpy, "rateValidation", event, 2L, 1L, applicantList);
    }
}
