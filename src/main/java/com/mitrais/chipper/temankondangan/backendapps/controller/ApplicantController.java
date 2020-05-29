package com.mitrais.chipper.temankondangan.backendapps.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.service.ApplicantService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Applicant Management System")
@RestController
@Validated
@RequestMapping("/applicant")
public class ApplicantController extends CommonResource {

	@Autowired
	private ApplicantService applicantService;

	@ApiOperation(value = "User accept one of the applicant in their event", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully accept the event applicant"),
			@ApiResponse(code = 401, message = "Full authentication is required to access this resource"),
			@ApiResponse(code = 400, message = "Error: This event has finished already\t\n "
					+ "Error: You cannot accept rejected applicant"),
			@ApiResponse(code = 404, message = "Applicant not found with id") })
	@PostMapping(value = "/accept")
	public ResponseEntity<ResponseBody> acceptEventApplicant(@RequestParam Long applicantId, HttpServletRequest request) {
		LOGGER.info("User accept one of the event applicant");
		
		applicantService.accept(applicantId);
		return ResponseEntity.ok(
				getResponseBody(HttpStatus.OK.value(), "Successfully accept the event applicant", request.getRequestURI()));

	}
	
	@ApiOperation(value = "User cancel the accepted applicant in their event", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully cancel the accepted applicant"),
			@ApiResponse(code = 401, message = "Full authentication is required to access this resource"),
			@ApiResponse(code = 400, message = " Error: You cannot cancel the accepted applicant 24 hours before event started \t\n "
					+ "Error: You cannot cancel non accepted applicant"),
			@ApiResponse(code = 404, message = "Applicant not found with id") })
	@PostMapping(value = "/cancel-accepted")
	public ResponseEntity<ResponseBody> cancelAcceptedApplicant(@RequestParam Long applicantId, HttpServletRequest request) {
		LOGGER.info("User cancel the accepted applicant in their event");
		
		applicantService.cancelAccepted(applicantId);
		return ResponseEntity.ok(
				getResponseBody(HttpStatus.OK.value(), "Successfully cancel the accepted applicant", request.getRequestURI()));

	}
	

	@ApiOperation(value = "User reject the applicant who applied to their event", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully reject the applied applicant"),
			@ApiResponse(code = 401, message = "Full authentication is required to access this resource"),
//			@ApiResponse(code = 400, message = " Error: You cannot cancel the accepted applicant 24 hours before event started \t\n "
//					+ "Error: You cannot cancel non accepted applicant"),
			@ApiResponse(code = 404, message = "Applicant not found with id") })
	@PostMapping(value = "/reject")
	public ResponseEntity<ResponseBody> rejectAppliedApplicant(@RequestParam Long applicantId, HttpServletRequest request) {
		LOGGER.info("User cancel the accepted applicant in their event");
		
		applicantService.rejectApplicant(applicantId);
		return ResponseEntity.ok(
				getResponseBody(HttpStatus.OK.value(), "Successfully reject the applied applicant", request.getRequestURI()));

	}
}