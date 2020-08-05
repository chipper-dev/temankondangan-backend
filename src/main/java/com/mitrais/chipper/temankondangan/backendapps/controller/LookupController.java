package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.Lookup;
import com.mitrais.chipper.temankondangan.backendapps.model.json.NotificationDataWrapper;
import com.mitrais.chipper.temankondangan.backendapps.service.LookupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "Lookup")
@RestController
@RequestMapping("/lookup")
public class LookupController extends CommonResource {

    @Autowired
    LookupService lookupService;

    @ApiOperation(value = "Get Lookup", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @GetMapping("/{lookupKey}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> getLookup(@PathVariable("lookupKey") String lookupKey, HttpServletRequest request) {
        List<Lookup> data = lookupService.getLookup(lookupKey);

        return ResponseEntity.ok(
                getResponseBody(HttpStatus.OK.value(), data, request.getRequestURI()));
    }
}
