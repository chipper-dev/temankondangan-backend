package com.mitrais.chipper.temankondangan.backendapps.exception;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        HashMap<String, String> hashError = new HashMap<>();
        hashError.put("Error", ex.getMessage());
        JSONObject json = new JSONObject();
        json.putAll(hashError);

        CommonResource resource = new CommonResource();
        return new ResponseEntity<Object>(
                resource.getResponseBody(HttpStatus.INTERNAL_SERVER_ERROR, null, json, request.getContextPath()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        HashMap<String, String> hashError = new HashMap<>();
        hashError.put(ex.getFieldName(), ex.getResourceName());
        JSONObject json = new JSONObject();
        json.putAll(hashError);

        CommonResource resource = new CommonResource();
        return new ResponseEntity<Object>(
                resource.getResponseBody(HttpStatus.NOT_FOUND, null, json, request.getContextPath()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        CommonResource resource = new CommonResource();
        return new ResponseEntity<Object>(
                resource.getResponseBody(HttpStatus.BAD_REQUEST, null, ex.getMessage(), request.getContextPath()),
                HttpStatus.BAD_REQUEST
        );
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
                HttpStatus.BAD_REQUEST
        );
    }
}
