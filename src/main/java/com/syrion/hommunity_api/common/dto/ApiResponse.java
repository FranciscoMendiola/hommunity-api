package com.syrion.hommunity_api.common.dto;

public class ApiResponse {

	private String message;

	public ApiResponse(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}
