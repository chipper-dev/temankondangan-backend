package com.mitrais.chipper.temankondangan.backendapps.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.api.client.http.HttpHeaders;
import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import net.minidev.json.JSONObject;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Set;

@RestControllerAdvice
public class CustomResponseEntityExceptionHandler {

	private static final String ERROR = "Error: ";

	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<Object> handleUserNotFoundException(ResourceNotFoundException ex,
			HttpServletRequest request) {
		HashMap<String, String> hashError = new HashMap<>();
		hashError.put(ex.getFieldName(), ex.getResourceName());
		JSONObject json = new JSONObject();
		json.putAll(hashError);

		CommonResource resource = new CommonResource();
		return new ResponseEntity<>(resource.getResponseBody(HttpStatus.NOT_FOUND, null, json, request.getRequestURI()),
				HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<>(
				resource.getResponseBody(HttpStatus.BAD_REQUEST, null, ex.getMessage(), request.getRequestURI()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public final ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<>(
				resource.getResponseBody(HttpStatus.UNAUTHORIZED, null, ex.getMessage(), request.getRequestURI()),
				HttpStatus.UNAUTHORIZED);
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
		return new ResponseEntity<>(resource.getResponseBody(HttpStatus.BAD_REQUEST, ex.getMessage(),
				"Error: Cannot send null values!", request.getRequestURI()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PropertyReferenceException.class)
	public final ResponseEntity<Object> handlePropertyReferenceException(PropertyReferenceException ex,
			HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<>(resource.getResponseBody(HttpStatus.BAD_REQUEST, null, ERROR + ex.getMessage(),
				request.getRequestURI()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DateTimeParseException.class)
	public final ResponseEntity<Object> handleDateTimeParseException(DateTimeParseException ex,
			HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<>(
				resource.getResponseBody(HttpStatus.BAD_REQUEST, null,
						"Error: '" + ex.getParsedString() + "' is not a valid format", request.getRequestURI()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TransactionSystemException.class)
	public final ResponseEntity<Object> handleTransactionSystemException(TransactionSystemException ex,
			HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		String message = ERROR + ex.getMessage();
		String path = request.getRequestURI();

		Throwable cause = ex.getRootCause();
		if (cause instanceof ConstraintViolationException) {
			Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) cause)
					.getConstraintViolations();
			httpStatus = HttpStatus.BAD_REQUEST;
			// handle Constraints in model
			for (ConstraintViolation<?> contraints : constraintViolations) {
				message = ERROR + contraints.getRootBeanClass().getSimpleName() + " " + contraints.getPropertyPath()
						+ " " + contraints.getMessage();

			}
			return new ResponseEntity<>(resource.getResponseBody(httpStatus, null, message, path), httpStatus);
		}

		return new ResponseEntity<>(resource.getResponseBody(httpStatus, null, message, path), httpStatus);

	}

	@ExceptionHandler(InvalidFormatException.class)
	public final ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex,
			HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<>(resource.getResponseBody(HttpStatus.BAD_REQUEST, ex.getMessage(),
				"Error: Invalid format values!", request.getRequestURI()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NumberFormatException.class)
	public final ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex,
			HttpServletRequest request) {
		CommonResource resource = new CommonResource();
		return new ResponseEntity<>(resource.getResponseBody(HttpStatus.BAD_REQUEST, ex.getMessage(),
				"Error: Cannot use the text value as parameter, please use the number format value!",
				request.getRequestURI()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public final ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
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
		return new ResponseEntity<>(
				resource.getResponseBody(HttpStatus.BAD_REQUEST, null, json, request.getContextPath()),
				HttpStatus.BAD_REQUEST);
	}
}
