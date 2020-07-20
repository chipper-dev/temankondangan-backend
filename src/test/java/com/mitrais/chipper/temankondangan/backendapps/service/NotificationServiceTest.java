package com.mitrais.chipper.temankondangan.backendapps.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
import com.mitrais.chipper.temankondangan.backendapps.model.Notification;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.repository.NotificationRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.NotificationServiceImpl;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotificationServiceTest {

	@Mock
	NotificationRepository notificationRepository;

	@InjectMocks
	NotificationServiceImpl notificationService;

	@BeforeEach
	public void init() {
		
	}

	// send notification service
//	@Test
//	public void sendTest() {
//		User user = User.builder().userId(1L).build();
//		Map<String, String> notifMap = new HashMap<String, String>();
//		notifMap.put("key", "value");
//		
//		Mockito.doNothing().when(notificationRepository.save(Mockito.any(Notification.class)));
//		
//		notificationService.send("notif title", "notif body", user, notifMap);
//		assertEquals(ApplicantStatus.ACCEPTED, applicant.getStatus());
//	}

}
