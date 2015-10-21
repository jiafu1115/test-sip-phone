package com.googlecode.test.phone.sip.handle.uas.incomingcall;

import javax.sip.RequestEvent;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.AbstractSipPhone;

public class IgnoreReceivedCallStrategy extends ReceivedCallStrategy {
	
	private static final Logger LOGGER=Logger.getLogger(IgnoreReceivedCallStrategy.class);
 	
	public IgnoreReceivedCallStrategy(AbstractSipPhone abstractSipPhone) {
		super(abstractSipPhone);
 	}


	@Override
	public void handle(RequestEvent requestEvent) {
		LOGGER.info("[SIP][ReceivedCall]: ignore the call:"+requestEvent.getRequest().getRequestURI());
 	}

}
