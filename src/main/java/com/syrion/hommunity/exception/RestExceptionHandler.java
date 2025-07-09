package com.syrion.hommunity.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ApiException.class)
	protected ResponseEntity<ExceptionResponse> handleApiException(ApiException exception, WebRequest request) {

		ExceptionResponse response = new ExceptionResponse();

		response.setTimestamp(LocalDateTime.now());
		response.setStatus(exception.getStatus().value());
		response.setError(exception.getStatus());
		response.setMessage(exception.getMessage());
		response.setPath(((ServletWebRequest) request).getRequest().getRequestURI().toString());

		return new ResponseEntity<>(response, response.getError());
	}

	@ExceptionHandler(DBAccessException.class)
	protected ResponseEntity<ExceptionResponse> DBAccessException(DBAccessException exception, WebRequest request) {

		System.out.println(exception.getException().getLocalizedMessage());

		ExceptionResponse response = new ExceptionResponse();
		response.setTimestamp(LocalDateTime.now());
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setError(HttpStatus.INTERNAL_SERVER_ERROR);
		response.setMessage("Error al consultar la base de datos: " + exception.getLocalizedMessage());
		response.setPath(((ServletWebRequest) request).getRequest().getRequestURI().toString());

		return new ResponseEntity<>(response, response.getError());
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ExceptionResponse> handleGenericException(Exception exception, WebRequest request) {

		exception.printStackTrace(); // Solo para depuración, quítar en producción

		ExceptionResponse response = new ExceptionResponse();
		response.setTimestamp(LocalDateTime.now());
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setError(HttpStatus.INTERNAL_SERVER_ERROR);
		response.setMessage("Error inesperado: " + exception.getLocalizedMessage());
		response.setPath(((ServletWebRequest) request).getRequest().getRequestURI().toString());

		return new ResponseEntity<>(response, response.getError());
	}
}