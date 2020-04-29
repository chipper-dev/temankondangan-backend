package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.LoginWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Api(value = "Auth")
@RestController
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
    public ResponseEntity<ResponseBody> login(@RequestBody LoginWrapper data,
                                              HttpServletRequest request) {
        boolean result = authService.login(data.getEmail(), data.getPassword());

        if (result) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    data.getEmail(), data.getPassword()
            ));
            String jwt = tokenProvider.createToken(authentication);
            return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), jwt, null));

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
            if (user.getUserId() != null) {
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        register.getEmail(), register.getPassword()
                ));
                String jwt = tokenProvider.createToken(authentication);
                return new ResponseEntity<>(
                        getResponseBody(HttpStatus.CREATED.value(), jwt, null),
                        HttpStatus.CREATED);

            } else {
                return new ResponseEntity<>(
                        getResponseBody(HttpStatus.UNPROCESSABLE_ENTITY, null, null, request.getRequestURI()),
                        HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(
                    getResponseBody(e.getStatus(), null, e.getReason(), request.getRequestURI()),
                    e.getStatus());
        }

    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @GetMapping("/logout")
    public ResponseEntity<ResponseBody> logout(HttpServletRequest request) {
        String token = getToken(request.getHeader("Authorization"));
        boolean result = authService.logout(tokenProvider.getUserIdFromToken(token));
        if (result) {
            return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, null));
        } else {
            return new ResponseEntity<>(
                    getResponseBody(HttpStatus.UNPROCESSABLE_ENTITY, null, null, request.getRequestURI()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
