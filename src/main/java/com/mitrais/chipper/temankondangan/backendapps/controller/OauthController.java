package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.json.OauthResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.OauthWrapper;
import com.mitrais.chipper.temankondangan.backendapps.service.OAuthService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(value = "OAuth", description = "Operations regarding authentication using Gmail in TemanKondangan System")
@RestController
@RequestMapping("/oauth")
public class OauthController extends CommonResource {

    @Autowired
    OAuthService oAuthService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> token(@RequestBody OauthWrapper data,
                                              HttpServletRequest request) {
        OauthResponseWrapper responseWrapper = oAuthService.getToken(data.getEmail(), data.getUid());

        if (responseWrapper != null)
            return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), responseWrapper, null));
        else
            return new ResponseEntity<>(
                    getResponseBody(HttpStatus.UNAUTHORIZED, null, null, request.getRequestURI()),
                    HttpStatus.UNAUTHORIZED);
    }
}
