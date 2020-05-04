package com.mitrais.chipper.temankondangan.backendapps.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mitrais.chipper.temankondangan.backendapps.model.json.EditEventWrapper;
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
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateEventWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.EventService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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

		try {

			Event result = eventService.create(userId, wrapper);
			return ResponseEntity.ok(getResponseBody(HttpStatus.CREATED.value(), result, null));

		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(getResponseBody(e.getStatus(), null, e.getReason(), request.getRequestURI()),
					e.getStatus());
		}
	}

	@ApiOperation(value = "Find all event", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@GetMapping(value = "/find-all")
	public ResponseEntity<ResponseBody> findAll(@RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "createdDate") String sortBy,
			@ApiParam(value = "input ASC or DESC") @RequestParam(defaultValue = "DESC") String direction,
			HttpServletRequest request) {
		LOGGER.info("Find all Event");

		try {
			List<Event> events = eventService.findAll(pageNumber, pageSize, sortBy, direction);

			return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(),
					getContentList(pageNumber, pageSize, events), request.getRequestURI()));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(getResponseBody(HttpStatus.BAD_REQUEST, null, null, request.getRequestURI()));
		}
	}

	@ApiOperation(value = "Edit Event", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/edit")
	public ResponseEntity<ResponseBody> edit(@RequestBody EditEventWrapper wrapper, HttpServletRequest request) {
		LOGGER.info("Edit an event");
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);

		try {

			Event result = eventService.edit(userId, wrapper);
			return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), result, null));

		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(getResponseBody(e.getStatus(), null, e.getReason(), request.getRequestURI()),
					e.getStatus());
		}
	}

}
