package com.googlecode.test.phone.sip;

import java.util.ArrayList;
import java.util.List;

import javax.sip.message.Request;
import javax.sip.message.Response;

public class ReceivedMessages {
	
	private List<Response> receivedResponses = new ArrayList<Response>();
	private List<Request> receivedRequests = new ArrayList<Request>();
	 
	public ReceivedMessages() {
		super();
 	}

	public List<Response> getReceivedResponses() {
		return receivedResponses;
	}

	public List<Request> getReceivedRequests() {
		return receivedRequests;
	}

	public List<Request> getReceivedRequests(String methodName) {
 		List<Request> receivedRequests = getReceivedRequests();
		List<Request> receivedRequestsForMethod = new ArrayList<Request>();
		for (Request request : receivedRequests) {
			if (request.getMethod().equalsIgnoreCase(methodName)) {
				receivedRequestsForMethod.add(request);
			}
		}
		return receivedRequestsForMethod;
	}
	 

}
