package com.googlecode.test.phone.sip.handle.uas;

import javax.sip.RequestEvent;

public interface RequestHandler {
	
	void handle(RequestEvent requestEvent) throws Exception;

}
