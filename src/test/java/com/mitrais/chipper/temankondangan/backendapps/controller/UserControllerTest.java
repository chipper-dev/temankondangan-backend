package com.mitrais.chipper.temankondangan.backendapps.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.exception.UnauthorizedException;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ForgotPasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ResetPasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.UserServiceImpl;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {
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
	UserServiceImpl userService;

	@Autowired
	public UserControllerTest(WebApplicationContext context, AuthenticationManager authenticationManager,
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
	public void changePasswordTest() throws Exception {
		UserChangePasswordWrapper wrapper = UserChangePasswordWrapper.builder().newPassword("@12345").build();

		Mockito.when(userService.changePassword(anyLong(), Mockito.any(UserChangePasswordWrapper.class)))
				.thenReturn(true);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/user/change-password")
				.header("Authorization", "Bearer " + token).content(asJsonString(wrapper))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").isBoolean()).andExpect(jsonPath("$.content").value(true));
	}

	@Test
	public void shouldThrowUnauthorizedException_whenUserAuthInvalidInChangePasswordTest() throws Exception {
		UserChangePasswordWrapper wrapper = UserChangePasswordWrapper.builder().newPassword("@12345").build();

		Mockito.when(userService.changePassword(anyLong(), Mockito.any(UserChangePasswordWrapper.class)))
				.thenThrow(UnauthorizedException.class);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/user/change-password")
				.header("Authorization", "Bearer " + token).content(asJsonString(wrapper))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error").value("Unauthorized"));
	}

	@Test
	public void shouldThrowResourceNotFoundException_inChangePasswordTest() throws Exception {
		UserChangePasswordWrapper wrapper = UserChangePasswordWrapper.builder().newPassword("@12345").build();

		Mockito.when(userService.changePassword(anyLong(), Mockito.any(UserChangePasswordWrapper.class)))
				.thenThrow(ResourceNotFoundException.class);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/user/change-password")
				.header("Authorization", "Bearer " + token).content(asJsonString(wrapper))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	public void shouldBadRequestException_inChangePasswordTest() throws Exception {
		UserChangePasswordWrapper wrapper = UserChangePasswordWrapper.builder().newPassword("@12345").build();

		Mockito.when(userService.changePassword(anyLong(), Mockito.any(UserChangePasswordWrapper.class)))
				.thenThrow(BadRequestException.class);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/user/change-password")
				.header("Authorization", "Bearer " + token).content(asJsonString(wrapper))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"));
	}

	@Test
	public void createPasswordTest() throws Exception {
		UserCreatePasswordWrapper wrapper = UserCreatePasswordWrapper.builder().newPassword("@12345").build();

		Mockito.when(userService.createPassword(anyLong(), Mockito.any(UserCreatePasswordWrapper.class)))
				.thenReturn(true);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/user/create-password")
				.header("Authorization", "Bearer " + token).content(asJsonString(wrapper))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content").isBoolean()).andExpect(jsonPath("$.content").value(true));
	}

	@Test
	public void removeUserTest() throws Exception {
		Mockito.doNothing().when(userService).remove(anyLong());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/user/remove").header("Authorization",
				"Bearer " + token);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void forgotPasswordTest() throws Exception {
		ForgotPasswordWrapper wrapper = ForgotPasswordWrapper.builder().email("emailtest@123").build();

		Mockito.doNothing().when(userService).forgotPassword(Mockito.anyString());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user/forgot-password")
				.header("Authorization", "Bearer " + token).content(asJsonString(wrapper))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content")
						.value("Verification code already sent to your email. Please check your email"));
	}

	@Test
	public void resetPasswordTest() throws Exception {
		ResetPasswordWrapper wrapper = ResetPasswordWrapper.builder().newPassword("newpassword@123").build();

		Mockito.doNothing().when(userService).resetPassword(Mockito.any(ResetPasswordWrapper.class));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user/reset-password")
				.header("Authorization", "Bearer " + token).content(asJsonString(wrapper))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.content").isNotEmpty())
				.andExpect(jsonPath("$.content")
						.value("Your password is updated successfully. Please try to login with your new password"));
	}

	@Test
	public void saveMessagingTokenTest() throws Exception {
		Mockito.doNothing().when(userService).saveMessagingToken(anyLong(), Mockito.anyString());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user/save-messaging-token")
				.header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
				.param("messagingToken", Mockito.anyString());

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
