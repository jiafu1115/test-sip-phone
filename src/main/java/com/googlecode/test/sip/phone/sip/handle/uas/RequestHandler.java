package com.googlecode.test.sip.phone.sip.handle.uas;

import javax.sip.RequestEvent;

public interface RequestHandler {
	
	void handle(RequestEvent requestEvent) throws Exception;

}
