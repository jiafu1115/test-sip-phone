package com.googlecode.test.phone;

public class ReferResult {
	
	private int statusCode;
	private String reason;
	
	public ReferResult(int statusCode, String reason) {
		super();
		this.statusCode = statusCode;
		this.reason = reason;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	 
}
