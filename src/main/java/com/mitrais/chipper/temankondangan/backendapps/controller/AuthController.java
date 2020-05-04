package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
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
    public ResponseEntity<ResponseBody> login(@RequestBody LoginWrapper data, HttpServletRequest request) {
        authService.login(data.getEmail(), data.getPassword());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword()));
        String jwt = tokenProvider.createToken(authentication);

        return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), jwt, null));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseBody> register(@RequestBody RegisterUserWrapper register,
                                                 HttpServletRequest request) {
        authService.save(register);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(register.getEmail(), register.getPassword()));
        String jwt = tokenProvider.createToken(authentication);

        return new ResponseEntity<>(getResponseBody(HttpStatus.CREATED.value(), jwt, null), HttpStatus.CREATED);
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @GetMapping("/logout")
    public ResponseEntity<ResponseBody> logout(HttpServletRequest request) {
        String token = getToken(request.getHeader("Authorization"));
        authService.logout(tokenProvider.getUserIdFromToken(token));

        return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, null));
    }
}
