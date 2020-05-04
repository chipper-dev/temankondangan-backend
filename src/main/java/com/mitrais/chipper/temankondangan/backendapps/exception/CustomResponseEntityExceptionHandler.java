package com.mitrais.chipper.temankondangan.backendapps.exception;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;

import net.minidev.json.JSONObject;

@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, HttpServletRequest request) {
		HashMap<String, String> hashError = new HashMap<>();
		hashError.put("Error", ex.getMessage());
		JSONObject json = new JSONObject();
		json.putAll(hashError);

		CommonResource resource = new CommonResource();
		return new ResponseEntity<Object>(
				resource.getResponseBody(HttpStatus.INTERNAL_SERVER_ERROR, null, json, request.getRequestURI()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<Object> handleUserNotFoundException(ResourceNotFoundException ex,
			HttpServletRequest request) {
		HashMap<String, String> hashError = new HashMap<>();
		hashError.put(ex.getFieldName(), ex.getResourceName());
		JSONObject json = new JSONObject();
		json.putAll(hashError);

		CommonResource resource = new CommonResource();
		return new ResponseEntity<Object>(
				resource.getResponseBody(HttpStatus.NOT_FOUND, null, json, request.getRequestURI()),
				HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<Object>(
				resource.getResponseBody(HttpStatus.BAD_REQUEST, null, ex.getMessage(), request.getRequestURI()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public final ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex,
			HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<>(
				resource.getResponseBody(ex.getStatus(), null, ex.getReason(), request.getRequestURI()),
				ex.getStatus());
	}

	@ExceptionHandler(NullPointerException.class)
	public final ResponseEntity<Object> handleNullPointerException(NullPointerException ex,
			HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<Object>(resource.getResponseBody(HttpStatus.BAD_REQUEST, ex.getMessage(),
				"Error: Cannot send null values!", request.getRequestURI()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PropertyReferenceException.class)
	public final ResponseEntity<Object> handlePropertyReferenceException(PropertyReferenceException ex,
			HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<Object>(resource.getResponseBody(HttpStatus.BAD_REQUEST, null,
				"Error: " + ex.getMessage(), request.getRequestURI()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DateTimeParseException.class)
	public final ResponseEntity<Object> handleDateTimeParseException(DateTimeParseException ex,
			HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<Object>(
				resource.getResponseBody(HttpStatus.BAD_REQUEST, null,
						"Error: '" + ex.getParsedString() + "' is not a valid format", request.getRequestURI()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TransactionSystemException.class)
	public final ResponseEntity<Object> handleTransactionSystemException(TransactionSystemException ex,
			HttpServletRequest request) {
		CommonResource resource = new CommonResource();

		Throwable cause = ((TransactionSystemException) ex).getRootCause();
		if (cause instanceof ConstraintViolationException) {
			Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) cause)
					.getConstraintViolations();
			// do something here

			for (ConstraintViolation<?> contraints : constraintViolations) {

				// handle Constraints in model
				return new ResponseEntity<Object>(resource.getResponseBody(
						HttpStatus.BAD_REQUEST, null, "Error: " + contraints.getRootBeanClass().getSimpleName() + " "
								+ contraints.getPropertyPath() + " " + contraints.getMessage(),
						request.getRequestURI()), HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<Object>(resource.getResponseBody(HttpStatus.INTERNAL_SERVER_ERROR, null,
				"Error: " + ex.getMessage(), request.getRequestURI()), HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		final HashMap<String, String> errors = new HashMap<>();
		for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.put(error.getObjectName(), error.getDefaultMessage());
		}

		JSONObject json = new JSONObject();
		json.putAll(errors);

		CommonResource resource = new CommonResource();
		return new ResponseEntity<Object>(
				resource.getResponseBody(HttpStatus.BAD_REQUEST, null, json, request.getContextPath()),
				HttpStatus.BAD_REQUEST);
	}
}
