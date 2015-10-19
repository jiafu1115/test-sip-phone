package com.googlecode.test.sip.phone.sip.handle.uac;

import javax.sip.ResponseEvent;

import com.googlecode.test.sip.phone.sip.phone.AbstractSipPhone;

public class ByeResponseHandler extends AbstractResponseHandler {

	public ByeResponseHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
	}

	@Override
	public void handle(ResponseEvent responseEvent) {
		sipPhone.stopRtpSession();
	}

}
