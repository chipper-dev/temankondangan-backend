package com.mitrais.chipper.temankondangan.backendapps.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.repository.ApplicantRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.ApplicantServiceImpl;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicantServiceTest {

	@Mock
	EventRepository eventRepository;

	@Mock
	ApplicantRepository applicantRepository;

	@InjectMocks
	ApplicantServiceImpl applicantService;

	private Applicant applicant;
	private Event event;

	@BeforeEach
	public void init() {
		event = new Event();
		event.setEventId(1L);
		event.setDataState(DataState.ACTIVE);
		event.setStartDateTime(null);
		event.setFinishDateTime(null);

		applicant = new Applicant();
		applicant.setId(1L);
		applicant.setDataState(DataState.ACTIVE);
		applicant.setEvent(null);
	}

	// accept applicant service
	@Test
	public void acceptApplicantTest() {
		event.setStartDateTime(LocalDateTime.now().plusHours(1));
		event.setFinishDateTime(LocalDateTime.now().plusHours(3));
		applicant.setEvent(event);

		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(applicant));

		Answer<Applicant> answer = new Answer<Applicant>() {
			public Applicant answer(InvocationOnMock invocation) throws Throwable {
				applicant.setStatus(ApplicantStatus.ACCEPTED);
				return applicant;
			}
		};

		doAnswer(answer).when(applicantRepository).save(Mockito.any(Applicant.class));
		applicantService.accept(1L);
		assertEquals(ApplicantStatus.ACCEPTED, applicant.getStatus());
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenApplicantNotFoundInAcceptApplicant() {
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> applicantService.accept(1L)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenEventNotFoundInAcceptApplicant() {
		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> applicantService.accept(1L)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenUserAcceptAfterEventHasFinishedAlready() {
		event.setFinishDateTime(LocalDateTime.now().minusHours(1));
		applicant.setEvent(event);

		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(applicant));

		assertThatThrownBy(() -> applicantService.accept(1L))
				.hasMessageContaining("Error: This event has finished already").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenUserAcceptApplicantWhoHasBeenRejected() {
		event.setStartDateTime(LocalDateTime.now().plusHours(2));
		event.setFinishDateTime(LocalDateTime.now().plusHours(3));
		applicant.setEvent(event);
		applicant.setStatus(ApplicantStatus.REJECTED);

		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(applicant));

		assertThatThrownBy(() -> applicantService.accept(1L))
				.hasMessageContaining("Error: You cannot accept rejected applicant")
				.isInstanceOf(BadRequestException.class);
	}

	// cancel accepted applicant service
	@Test
	public void cancelAcceptedTest() {
		event.setStartDateTime(LocalDateTime.now().plusHours(26));
		event.setFinishDateTime(LocalDateTime.now().plusHours(29));
		applicant.setEvent(event);
		applicant.setStatus(ApplicantStatus.ACCEPTED);

		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(applicant));

		Answer<Applicant> answer = new Answer<Applicant>() {
			public Applicant answer(InvocationOnMock invocation) throws Throwable {
				applicant.setStatus(ApplicantStatus.APPLIED);
				return applicant;
			}
		};

		doAnswer(answer).when(applicantRepository).save(Mockito.any(Applicant.class));
		applicantService.cancelAccepted(1L);
		assertEquals(ApplicantStatus.APPLIED, applicant.getStatus());
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenApplicantNotFoundInCancelAcceptedApplicant() {
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> applicantService.cancelAccepted(1L)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenEventNotFoundInCancelAcceptedApplicant() {
		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> applicantService.cancelAccepted(1L)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenUserCancelAcceptedApplicantAfterEventHasFinishedAlready() {
		event.setFinishDateTime(LocalDateTime.now().minusHours(1));
		applicant.setEvent(event);

		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(applicant));

		assertThatThrownBy(() -> applicantService.cancelAccepted(1L))
				.hasMessageContaining("Error: This event has finished already").isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenCancelAcceptedApplicant24HoursBeforeEventStarted() {
		event.setStartDateTime(LocalDateTime.now().plusHours(23));
		event.setFinishDateTime(LocalDateTime.now().plusHours(26));
		applicant.setEvent(event);

		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(applicant));

		assertThatThrownBy(() -> applicantService.cancelAccepted(1L))
				.hasMessageContaining("Error: You cannot cancel the accepted applicant 24 hours before event started")
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenCancelAcceptedApplicantDoNotHaveAcceptedStatus() {
		event.setStartDateTime(LocalDateTime.now().plusHours(26));
		event.setFinishDateTime(LocalDateTime.now().plusHours(29));
		applicant.setEvent(event);
		applicant.setStatus(ApplicantStatus.REJECTED);

		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(applicant));

		assertThatThrownBy(() -> applicantService.cancelAccepted(1L))
				.hasMessageContaining("Error: You cannot cancel non accepted applicant")
				.isInstanceOf(BadRequestException.class);
	}

	// reject applied applicant service
	@Test
	public void rejectAppliedApplicantTest() {
		event.setStartDateTime(LocalDateTime.now().plusHours(26));
		event.setFinishDateTime(LocalDateTime.now().plusHours(29));
		applicant.setEvent(event);
		applicant.setStatus(ApplicantStatus.APPLIED);

		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(applicant));

		Answer<Applicant> answer = new Answer<Applicant>() {
			public Applicant answer(InvocationOnMock invocation) throws Throwable {
				applicant.setStatus(ApplicantStatus.REJECTED);
				return applicant;
			}
		};

		doAnswer(answer).when(applicantRepository).save(Mockito.any(Applicant.class));
		applicantService.rejectApplicant(1L);
		assertEquals(ApplicantStatus.REJECTED, applicant.getStatus());
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenApplicantNotFoundInRejectApplicant() {
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> applicantService.rejectApplicant(1L)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void shouldThrowResourceNotFoundException_WhenEventNotFoundInRejectApplicant() {
		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenThrow(ResourceNotFoundException.class);
		assertThatThrownBy(() -> applicantService.rejectApplicant(1L)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void shouldThrowBadRequestException_WhenUserRejectApplicantAfterEventHasFinishedAlready() {
		event.setFinishDateTime(LocalDateTime.now().minusHours(1));
		applicant.setEvent(event);

		Mockito.when(eventRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(event));
		Mockito.when(applicantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(applicant));

		assertThatThrownBy(() -> applicantService.rejectApplicant(1L))
				.hasMessageContaining("Error: This event has finished already").isInstanceOf(BadRequestException.class);
	}

}
