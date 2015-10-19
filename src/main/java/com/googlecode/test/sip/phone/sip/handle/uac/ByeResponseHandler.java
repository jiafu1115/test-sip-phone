package com.googlecode.test.sip.phone.sip.handle.uac;

import javax.sip.ResponseEvent;

import org.apache.log4j.Logger;

import com.googlecode.test.sip.phone.sip.phone.AbstractSipPhone;

public class ByeResponseHandler extends AbstractResponseHandler {
	
	private static final Logger LOG = Logger.getLogger(ByeResponseHandler.class);

 	public ByeResponseHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
 	}

	@Override
	public void handle(ResponseEvent responseEvent) {
    		sipPhone.stopRtpSession();
 		 
	}

}
