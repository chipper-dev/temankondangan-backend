package com.mitrais.chipper.temankondangan.backendapps.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.ApplicantServiceImpl;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicantControllerTest {
	private MockMvc mockMvc;
	private WebApplicationContext context;
	private AuthenticationManager authenticationManager;
	private TokenProvider tokenProvider;
	private PasswordEncoder passwordEncoder;

	private String token;
	private User user;
	private final String email = "test@mail.com";
	private final String password = "test123!";

	@MockBean
	UserRepository userRepository;

	@MockBean
	ApplicantServiceImpl applicantService;

	@Autowired
	public ApplicantControllerTest(WebApplicationContext context, AuthenticationManager authenticationManager,
			TokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
		this.context = context;
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
		this.passwordEncoder = passwordEncoder;
	}

	@BeforeAll
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

	}

	@BeforeEach
	public void initTest() {
		user = User.builder().userId(1L).email(email).passwordHashed(passwordEncoder.encode(password)).build();

		Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		token = tokenProvider.createToken(authentication);
	}

	@Test
	public void acceptEventApplicantTest() throws Exception {
		Mockito.doNothing().when(applicantService).accept(anyLong(), anyLong());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/applicant/accept?applicantId=1")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").value("Successfully accept the event applicant"));
	}

	@Test
	public void cancelAcceptedApplicantTest() throws Exception {
		Mockito.doNothing().when(applicantService).cancelAccepted(anyLong(), anyLong());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/applicant/cancel-accepted?applicantId=1")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").value("Successfully cancel the accepted applicant"));
	}

	@Test
	public void rejectApplicantTest() throws Exception {
		Mockito.doNothing().when(applicantService).rejectApplicant(anyLong(), anyLong());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/applicant/reject?applicantId=1")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").value("Successfully reject the applied applicant"));
	}
}
