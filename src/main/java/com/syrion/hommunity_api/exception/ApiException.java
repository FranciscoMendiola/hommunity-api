package com.syrion.hommunity_api.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private HttpStatus status;
	
	public ApiException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}