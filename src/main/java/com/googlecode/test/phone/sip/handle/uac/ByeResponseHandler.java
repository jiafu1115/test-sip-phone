package com.googlecode.test.phone.sip.handle.uac;

import javax.sip.ResponseEvent;

import com.googlecode.test.phone.AbstractSipPhone;

public class ByeResponseHandler extends AbstractResponseHandler {

	public ByeResponseHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
	}

	@Override
	public void handle(ResponseEvent responseEvent) {
		String dialogId = responseEvent.getDialog().getDialogId();
		sipPhone.stopRtpSession(dialogId);
	}

}
