package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.common.Constants;
import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.UnauthorizedException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.Rating;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.RatingType;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RatingWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void getUserRatingDataTest() {
        List<Rating> ratingList = Arrays.asList(
                Rating.builder().userId(1L).score(3).build(),
                Rating.builder().userId(1L).score(4).build(),
                Rating.builder().userId(1L).score(4).build(),
                Rating.builder().userId(1L).score(3).build()
        );

        Mockito.when(ratingRepository.findByUserId(anyLong())).thenReturn(ratingList);
        HashMap<String, Double> dataRating = ratingService.getUserRating(1L);

        assertEquals(3.5, dataRating.get(Constants.RatingDataKey.AVG));
        assertEquals(4.0, dataRating.get(Constants.RatingDataKey.TOT));
    }

    @Test
    public void isRatedTrueTest() {
        List<Rating> ratingList = Arrays.asList(
                Rating.builder().userId(1L).eventId(2L).score(3).build()
        );

        Mockito.when(ratingRepository.findByUserAndEventId(anyLong(), anyLong())).thenReturn(ratingList);

        assertTrue(ratingService.isRated(1L, 2L));
    }


    @Test
    public void showRatingTest() {
        List<Rating> ratingList = Arrays.asList(
                Rating.builder().userId(1L).eventId(2L).userVoterId(3L).score(3).build()
        );

        Mockito.when(ratingRepository.findByUserVoterAndEventId(anyLong(), anyLong())).thenReturn(ratingList);
        RatingWrapper ratingWrapper = ratingService.showRating(2L, 3L);
        assertSame(3, ratingWrapper.getScore());
        assertSame(1L, ratingWrapper.getUserId());
    }
}
