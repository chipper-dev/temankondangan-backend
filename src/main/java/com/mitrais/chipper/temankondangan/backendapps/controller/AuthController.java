package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.AuthService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Api(value="Register", description="Operations regarding registering in TemenKondangan System")
@RestController
@Validated
@RequestMapping("/auth")
public class AuthController extends CommonResource {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AuthService authService;

    @Autowired
    TokenProvider tokenProvider;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> login(@RequestParam("email") String email,
                                              @RequestParam("password") String password,
                                              HttpServletRequest request) {
        boolean result = authService.login(email, password);

        if (result) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    email, password
            ));
            String jwt = tokenProvider.createToken(authentication);
            return ResponseEntity.ok(getResponseBody(HttpStatus.CREATED.value(), jwt, null));

        } else {
            return new ResponseEntity<>(
                    getResponseBody(HttpStatus.UNAUTHORIZED, null, null, request.getRequestURI()),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseBody> register(@RequestBody RegisterUserWrapper register, HttpServletRequest request) {
        try {
            User user = authService.save(register);
            return ResponseEntity.ok(getResponseBody(HttpStatus.CREATED.value(), user, null));

        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(
                    getResponseBody(e.getStatus(), null, e.getReason(), request.getRequestURI()),
                    e.getStatus());
        }

    }
}
