package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ApplicantStatus;
import com.mitrais.chipper.temankondangan.backendapps.model.json.AppliedEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.UserRepository;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.impl.EventServiceImpl;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventControllerTest {
    private MockMvc mvc;
    private WebApplicationContext context;
    private AuthenticationManager authenticationManager;
    private TokenProvider tokenProvider;
    private PasswordEncoder passwordEncoder;

    private String token;
    private User user;
    private final String email = "test@mail.com";
    private final String password = "test123!";

    @MockBean
    private UserRepository userRepository;

    @MockBean
    EventServiceImpl eventService;

    @Autowired
    public EventControllerTest(WebApplicationContext context, AuthenticationManager authenticationManager, TokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.context = context;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeAll
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void initTest() {
        user = User.builder().userId(1L).email(email).passwordHashed(passwordEncoder.encode(password)).build();

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        token = tokenProvider.createToken(authentication);
    }

    @Test
    public void findCurrentAppliedEventTest() throws Exception {
        AppliedEventWrapper appliedEventWrapper = AppliedEventWrapper.builder()
                .photoProfileUrl("image.jpg")
                .title("Lorem Ipsum")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .finishDateTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .city("Sim City")
                .status(ApplicantStatus.APPLIED)
                .build();

        List<AppliedEventWrapper> wrapperList = Arrays.asList(appliedEventWrapper);

        Mockito.when(eventService.findActiveAppliedEvent(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())).thenReturn(wrapperList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/event/my-applied-event-current")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].photoProfileUrl").value("image.jpg"))
                .andExpect(jsonPath("$.content[0].title").value("Lorem Ipsum"));
    }

    @Test
    public void findPastAppliedEventTest() throws Exception {
        AppliedEventWrapper appliedEventWrapper = AppliedEventWrapper.builder()
                .photoProfileUrl("image.jpg")
                .title("Lorem Ipsum")
                .startDateTime(LocalDateTime.now().minusDays(1))
                .finishDateTime(LocalDateTime.now().minusDays(1).plusHours(1))
                .city("Sim City")
                .status(ApplicantStatus.APPLIED)
                .build();

        List<AppliedEventWrapper> wrapperList = Arrays.asList(appliedEventWrapper);

        Mockito.when(eventService.findPastAppliedEvent(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())).thenReturn(wrapperList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/event/my-applied-event-past")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].photoProfileUrl").value("image.jpg"))
                .andExpect(jsonPath("$.content[0].title").value("Lorem Ipsum"));
    }
}
