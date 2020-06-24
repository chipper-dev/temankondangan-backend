package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.UnauthorizedException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.RatingType;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RatingWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.RatingRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @BeforeEach
    public void setup() {
        User userCreator = User.builder().userId(1L).email("ini@mail.com").build();
        User userApplicant = User.builder().userId(2L).email("ini@mail.com").build();
        Event event = Event.builder().eventId(3L).user(userCreator).title("Lorem Ipsum").city("Jakarta").cancelled(false).build();

        List<Applicant> applicantList = Arrays.asList(
                Applicant.builder().applicantUser(userApplicant).status(ApplicantStatus.ACCEPTED).event(event).build()
        );

        Mockito.when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        Mockito.when(applicantRepository.findByEventIdAccepted(anyLong())).thenReturn(applicantList);
    }

    @Test
    public void scoreRatingMoreThanFive() throws Exception {
        RatingWrapper ratingWrapper = RatingWrapper.builder().score(7).userId(1L).ratingType(RatingType.APPLICANT).build();

        assertThatThrownBy(() -> ratingService.sendRating(3L, 1L, ratingWrapper))
                .hasMessageContaining("Error: Rating score is out of scope. Please use score from 1 to 5")
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void scoreRatingLowerThanOne() throws Exception {
        RatingWrapper ratingWrapper = RatingWrapper.builder().score(0).userId(2L).ratingType(RatingType.APPLICANT).build();

        assertThatThrownBy(() -> ratingService.sendRating(3L, 1L, ratingWrapper))
                .hasMessageContaining("Error: Rating score is out of scope. Please use score from 1 to 5")
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void scoreRatingApplicantNotMatch() throws Exception {
        RatingWrapper ratingWrapper = RatingWrapper.builder().score(4).userId(1L).ratingType(RatingType.APPLICANT).build();

        assertThatThrownBy(() -> ratingService.sendRating(3L, 1L, ratingWrapper))
                .hasMessageContaining("Error: Event applicant doesn't match!")
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void scoreRatingCreatorNotMatch() throws Exception {
        RatingWrapper ratingWrapper = RatingWrapper.builder().score(4).userId(2L).ratingType(RatingType.APPLICANT).build();

        assertThatThrownBy(() -> ratingService.sendRating(3L, 2L, ratingWrapper))
                .hasMessageContaining("Error: Event creator doesn't match!")
                .isInstanceOf(UnauthorizedException.class);
    }
}
