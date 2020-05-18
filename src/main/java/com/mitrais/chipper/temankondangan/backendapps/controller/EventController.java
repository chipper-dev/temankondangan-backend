package com.mitrais.chipper.temankondangan.backendapps.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventDetailResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllListResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.EventFindAllResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.EventService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Event Management System")
@RestController
@Validated
@RequestMapping("/event")
public class EventController extends CommonResource {

	@Autowired
	private EventService eventService;

	@Autowired
	private TokenProvider tokenProvider;

	@ApiOperation(value = "Create Event", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/create")
	public ResponseEntity<ResponseBody> create(@RequestBody CreateEventWrapper wrapper, HttpServletRequest request) {
		LOGGER.info("Create an event");
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);

		Event result = eventService.create(userId, wrapper);
		return ResponseEntity.ok(getResponseBody(HttpStatus.CREATED.value(), result, null));

	}

	@ApiOperation(value = "Find all event", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@ApiResponses(value = { @ApiResponse(response = EventFindAllListResponseWrapper.class, code = 200, message = "") })
	@GetMapping(value = "/find-all")
	public ResponseEntity<ResponseBody> findAll(@RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@ApiParam(value = "input createdDate or startDateTime") @RequestParam(defaultValue = "createdDate") String sortBy,
			@ApiParam(value = "input ASC or DESC") @RequestParam(defaultValue = "DESC") String direction,
			HttpServletRequest request) {
		LOGGER.info("Find all Event");
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);

		EventFindAllResponseWrapper events = eventService.findAll(pageNumber, pageSize, sortBy, direction, userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), events, request.getRequestURI()));
	}

	@ApiOperation(value = "Edit Event", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/edit")
	public ResponseEntity<ResponseBody> edit(@RequestBody EditEventWrapper wrapper, HttpServletRequest request) {
		LOGGER.info("Edit an event");
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);

		Event result = eventService.edit(userId, wrapper);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), result, null));

	}

	@ApiOperation(value = "Find Event Detail", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@GetMapping(value = "/find")
	public ResponseEntity<ResponseBody> find(@RequestParam String eventId, HttpServletRequest request) {
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);

		EventDetailResponseWrapper responseWrapper = eventService.findEventDetail(eventId, userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), responseWrapper, request.getRequestURI()));

	}

	@ApiOperation(value = "User apply to event", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping(value = "/apply")
	public ResponseEntity<ResponseBody> applyEvent(@RequestParam Long eventId, HttpServletRequest request) {
		LOGGER.info("A user apply to an event");
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);

		eventService.apply(userId, eventId);
		return ResponseEntity.ok(
				getResponseBody(HttpStatus.OK.value(), "Successfully applied to the event", request.getRequestURI()));

	}

	@ApiOperation(value = "User cancel to event", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping(value = "/cancel")
	public ResponseEntity<ResponseBody> cancelEvent(@RequestParam Long eventId, HttpServletRequest request) {
		LOGGER.info("A user cancel to an event");
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);
		eventService.cancelEvent(userId, eventId);
		return ResponseEntity.ok(
				getResponseBody(HttpStatus.OK.value(), "The event was canceled successfully", request.getRequestURI()));
	}
}
