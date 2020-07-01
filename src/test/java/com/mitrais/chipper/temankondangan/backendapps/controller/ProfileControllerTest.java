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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateProfileWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileCreatorResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.ProfileServiceImpl;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfileControllerTest {
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
	ProfileServiceImpl profileService;

	@Autowired
	public ProfileControllerTest(WebApplicationContext context, AuthenticationManager authenticationManager,
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
	public void updateProfileTest() throws Exception {
		Profile profile = Profile.builder().profileId(1L).build();

		Mockito.when(profileService.update(anyLong(), Mockito.any(ProfileUpdateWrapper.class))).thenReturn(profile);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/profile/update")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content.profileId").value(1L));
	}

	@Test
	public void findProfileTest() throws Exception {
		ProfileResponseWrapper responseWrapper = ProfileResponseWrapper.builder().profileId(1L).build();

		Mockito.when(profileService.findByUserId(anyLong())).thenReturn(responseWrapper);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/profile/find").header("Authorization",
				"Bearer " + token);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content.profileId").value(1L));
	}

	@Test
	public void findProfileCreatorTest() throws Exception {
		ProfileCreatorResponseWrapper responseWrapper = ProfileCreatorResponseWrapper.builder().fullName("name test")
				.build();

		Mockito.when(profileService.findOtherPersonProfile(anyLong())).thenReturn(responseWrapper);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/profile/find-profile/1")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content.fullName").value("name test"));
	}

	@Test
	public void registerUserTest() throws Exception {
		CreateProfileWrapper responseWrapper = CreateProfileWrapper.builder().fullname("name test")
				.build();

		Mockito.when(profileService.create(Mockito.any(CreateProfileWrapper.class))).thenReturn(new Profile());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/profile/create")
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(responseWrapper)).accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").value("Your profile created successfully"));
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
