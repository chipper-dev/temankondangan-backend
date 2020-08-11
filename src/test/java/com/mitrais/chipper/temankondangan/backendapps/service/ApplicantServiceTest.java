package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ProfileRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.ApplicantServiceImpl;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicantServiceTest {

	@Mock
	EventRepository eventRepository;

	@Mock
	ApplicantRepository applicantRepository;

	@Mock
	NotificationServiceImpl notificationService;

	@Mock
	ProfileRepository profileRepository;

	@InjectMocks
	ApplicantServiceImpl applicantService;

	private Applicant applicant;
	private Event event;
	private User user;
	private static Long userId = 1L;

	@BeforeEach
	public void init() {
		user = new User();
		user.setUserId(1L);

		event = new Event();
		event.setEventId(1L);
		event.setDataState(DataState.ACTIVE);
		event.setStartDateTime(null);
		event.setFinishDateTime(null);
		event.setUser(user);

		applicant = new Applicant();
		applicant.setId(1L);
		applicant.setDataState(DataState.ACTIVE);
		applicant.setEvent(event);
	}

	// accept applicant service
//	@Test
//	public void acceptApplicantTest() {
//		event.setStartDateTime(LocalDateTime.now().plusHours(1));
//		event.setFinishDateTime(LocalDateTime.now().plusHours(3));
//		applicant.setEvent(event);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//		Mockito.when(applicantRepository.existsByEventAndStatus(Mockito.any(Event.class),
//				Mockito.any(ApplicantStatus.class))).thenReturn(false);
//		Answer<Applicant> answer = new Answer<Applicant>() {
//			public Applicant answer(InvocationOnMock invocation) throws Throwable {
//				applicant.setStatus(ApplicantStatus.ACCEPTED);
//				return applicant;
//			}
//		};
//
//		doAnswer(answer).when(applicantRepository).save(Mockito.any(Applicant.class));
//		applicantService.accept(1L, userId);
//		assertEquals(ApplicantStatus.ACCEPTED, applicant.getStatus());
//	}
//
//	@Test
//	public void shouldThrowResourceNotFoundException_WhenApplicantNotFoundInAcceptApplicant() {
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenThrow(ResourceNotFoundException.class);
//		assertThatThrownBy(() -> applicantService.accept(1L, userId)).isInstanceOf(ResourceNotFoundException.class);
//	}
//
//	@Test
//	public void shouldThrowResourceNotFoundException_WhenEventNotFoundInAcceptApplicant() {
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenThrow(ResourceNotFoundException.class);
//		assertThatThrownBy(() -> applicantService.accept(1L, userId)).isInstanceOf(ResourceNotFoundException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenUserLoginIsNotTheCreatorEventInAcceptApplicant() {
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.accept(5L, 1L))
//				.hasMessageContaining("Error: Non event creator cannot do this")
//				.isInstanceOf(BadRequestException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenUserAcceptAfterEventHasFinishedAlready() {
//		event.setFinishDateTime(LocalDateTime.now().minusHours(1));
//		applicant.setEvent(event);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.accept(1L, userId))
//				.hasMessageContaining("Error: This event has finished already").isInstanceOf(BadRequestException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenUserAcceptApplicantWhoHasBeenRejected() {
//		event.setStartDateTime(LocalDateTime.now().plusHours(2));
//		event.setFinishDateTime(LocalDateTime.now().plusHours(3));
//		applicant.setEvent(event);
//		applicant.setStatus(ApplicantStatus.REJECTED);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.accept(1L, userId))
//				.hasMessageContaining("Error: You cannot accept rejected applicant")
//				.isInstanceOf(BadRequestException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenUserAcceptMoreThanOneApplicant() {
//		event.setStartDateTime(LocalDateTime.now().plusHours(2));
//		event.setFinishDateTime(LocalDateTime.now().plusHours(3));
//		applicant.setEvent(event);
//		applicant.setStatus(ApplicantStatus.ACCEPTED);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//		Mockito.when(applicantRepository.existsByEventAndStatus(Mockito.any(Event.class),
//				Mockito.any(ApplicantStatus.class))).thenReturn(true);
//
//		assertThatThrownBy(() -> applicantService.accept(1L, userId))
//				.hasMessageContaining("Error: You already have accepted applicant")
//				.isInstanceOf(BadRequestException.class);
//	}
//
//	// cancel accepted applicant service
//	@Test
//	public void cancelAcceptedTest() {
//		event.setStartDateTime(LocalDateTime.now().plusHours(26));
//		event.setFinishDateTime(LocalDateTime.now().plusHours(29));
//		applicant.setEvent(event);
//		applicant.setStatus(ApplicantStatus.ACCEPTED);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		Answer<Applicant> answer = new Answer<Applicant>() {
//			public Applicant answer(InvocationOnMock invocation) throws Throwable {
//				applicant.setStatus(ApplicantStatus.APPLIED);
//				return applicant;
//			}
//		};
//
//		doAnswer(answer).when(applicantRepository).save(Mockito.any(Applicant.class));
//		applicantService.cancelAccepted(1L, userId);
//		assertEquals(ApplicantStatus.APPLIED, applicant.getStatus());
//	}
//
//	@Test
//	public void shouldThrowResourceNotFoundException_WhenApplicantNotFoundInCancelAcceptedApplicant() {
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenThrow(ResourceNotFoundException.class);
//		assertThatThrownBy(() -> applicantService.cancelAccepted(1L, userId))
//				.isInstanceOf(ResourceNotFoundException.class);
//	}
//
//	@Test
//	public void shouldThrowResourceNotFoundException_WhenEventNotFoundInCancelAcceptedApplicant() {
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenThrow(ResourceNotFoundException.class);
//		assertThatThrownBy(() -> applicantService.cancelAccepted(1L, userId))
//				.isInstanceOf(ResourceNotFoundException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenUserLoginIsNotTheCreatorEventInCancelAcceptedApplicant() {
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.cancelAccepted(5L, 1L))
//				.hasMessageContaining("Error: Non event creator cannot do this")
//				.isInstanceOf(BadRequestException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenUserCancelAcceptedApplicantAfterEventHasFinishedAlready() {
//		event.setFinishDateTime(LocalDateTime.now().minusHours(1));
//		applicant.setEvent(event);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.cancelAccepted(1L, userId))
//				.hasMessageContaining("Error: This event has finished already").isInstanceOf(BadRequestException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenCancelAcceptedApplicant24HoursBeforeEventStarted() {
//		event.setStartDateTime(LocalDateTime.now().plusHours(23));
//		event.setFinishDateTime(LocalDateTime.now().plusHours(26));
//		applicant.setEvent(event);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.cancelAccepted(1L, userId))
//				.hasMessageContaining("Error: You cannot cancel the accepted applicant 24 hours before event started")
//				.isInstanceOf(BadRequestException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenCancelAcceptedApplicantDoNotHaveAcceptedStatus() {
//		event.setStartDateTime(LocalDateTime.now().plusHours(26));
//		event.setFinishDateTime(LocalDateTime.now().plusHours(29));
//		applicant.setEvent(event);
//		applicant.setStatus(ApplicantStatus.REJECTED);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.cancelAccepted(1L, userId))
//				.hasMessageContaining("Error: You cannot cancel non accepted applicant")
//				.isInstanceOf(BadRequestException.class);
//	}
//
//	// reject applied applicant service
//	@Test
//	public void rejectAppliedApplicantTest() {
//		event.setStartDateTime(LocalDateTime.now().plusHours(26));
//		event.setFinishDateTime(LocalDateTime.now().plusHours(29));
//		applicant.setEvent(event);
//		applicant.setStatus(ApplicantStatus.APPLIED);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		Answer<Applicant> answer = new Answer<Applicant>() {
//			public Applicant answer(InvocationOnMock invocation) throws Throwable {
//				applicant.setStatus(ApplicantStatus.REJECTED);
//				return applicant;
//			}
//		};
//
//		doAnswer(answer).when(applicantRepository).save(Mockito.any(Applicant.class));
//		applicantService.rejectApplicant(1L, userId);
//		assertEquals(ApplicantStatus.REJECTED, applicant.getStatus());
//	}
//
//	@Test
//	public void shouldThrowResourceNotFoundException_WhenApplicantNotFoundInRejectApplicant() {
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenThrow(ResourceNotFoundException.class);
//		assertThatThrownBy(() -> applicantService.rejectApplicant(1L, userId))
//				.isInstanceOf(ResourceNotFoundException.class);
//	}
//
//	@Test
//	public void shouldThrowResourceNotFoundException_WhenEventNotFoundInRejectApplicant() {
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenThrow(ResourceNotFoundException.class);
//		assertThatThrownBy(() -> applicantService.rejectApplicant(1L, userId))
//				.isInstanceOf(ResourceNotFoundException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenUserLoginIsNotTheCreatorEventInRejectApplicant() {
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.rejectApplicant(5L, 1L))
//				.hasMessageContaining("Error: Non event creator cannot do this")
//				.isInstanceOf(BadRequestException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenUserRejectApplicantAfterEventHasFinishedAlready() {
//		event.setFinishDateTime(LocalDateTime.now().minusHours(1));
//		applicant.setEvent(event);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.rejectApplicant(1L, userId))
//				.hasMessageContaining("Error: This event has finished already").isInstanceOf(BadRequestException.class);
//	}
//
//	@Test
//	public void shouldThrowBadRequestException_WhenUserRejectAcceptedApplicant() {
//		event.setStartDateTime(LocalDateTime.now().plusHours(26));
//		event.setFinishDateTime(LocalDateTime.now().plusHours(29));
//		applicant.setEvent(event);
//		applicant.setStatus(ApplicantStatus.ACCEPTED);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.rejectApplicant(1L, userId))
//				.hasMessageContaining("Error: You cannot reject the accepted applicant")
//				.isInstanceOf(BadRequestException.class);
//	}
//	
//
//	@Test
//	public void shouldThrowBadRequestException_WhenUserAlreadyRejectApplicant() {
//		event.setStartDateTime(LocalDateTime.now().plusHours(26));
//		event.setFinishDateTime(LocalDateTime.now().plusHours(29));
//		applicant.setEvent(event);
//		applicant.setStatus(ApplicantStatus.REJECTED);
//
//		Mockito.when(eventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
//		Mockito.when(applicantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(applicant));
//
//		assertThatThrownBy(() -> applicantService.rejectApplicant(1L, userId))
//				.hasMessageContaining("Error: You have rejected this applicant")
//				.isInstanceOf(BadRequestException.class);
//	}
}
