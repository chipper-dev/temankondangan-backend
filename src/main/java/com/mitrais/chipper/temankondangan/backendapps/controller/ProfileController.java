package com.mitrais.chipper.temankondangan.backendapps.controller;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.ProfileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Profile Management System", description = "Operations regarding profile in TemenKondangan System")
@RestController
@Validated
@RequestMapping("/profile")
public class ProfileController extends CommonResource {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private TokenProvider tokenProvider;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    @ApiOperation(value = "Update Optional Profile", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @PostMapping("/update")
    public ResponseEntity<ResponseBody> update(@RequestParam(value = "file", required = false) MultipartFile file,
                                               @RequestParam(value = "fullName", required = true) String fullName,
                                               @RequestParam(value = "dob", required = true) String dob,
                                               @RequestParam(value = "gender", required = true) Gender gender,
                                               @RequestParam(value = "city", required = false) String city,
                                               @RequestParam(value = "aboutMe", required = false) String aboutMe,
                                               @RequestParam(value = "interest", required = false) String interest, HttpServletRequest request)
            throws ParseException, IOException {
        LOGGER.info("Update profile");
        String token = getToken(request.getHeader("Authorization"));
        Long userId = tokenProvider.getUserIdFromToken(token);

        try {

            Profile result = profileService
                    .update(new ProfileUpdateWrapper(file, userId, fullName,
                            LocalDate.parse(dob, formatter), gender, city, aboutMe, interest));

            return ResponseEntity.ok(getResponseBody(HttpStatus.CREATED.value(), result, null));

        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(getResponseBody(e.getStatus(), null, e.getReason(), request.getRequestURI()),
                    e.getStatus());
        }
    }

    @ApiOperation(value = "Get Profile From Token", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @GetMapping("/find")
    public ResponseEntity<ResponseBody> findByUserId(HttpServletRequest request) {
        try {
            LOGGER.info("Find a profile from token");
            String token = getToken(request.getHeader("Authorization"));
            Long userId = tokenProvider.getUserIdFromToken(token);

            Profile profile = profileService.findByUserId(userId)
                    .orElseThrow(() -> new NoSuchElementException("No profile with user id : " + userId));
            return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), profile, request.getRequestURI()));
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(
                    getResponseBody(HttpStatus.NOT_FOUND, "No profile from token ", null, request.getRequestURI()),
                    HttpStatus.NOT_FOUND);
        }
    }
}
