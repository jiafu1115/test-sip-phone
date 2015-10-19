package com.googlecode.test.sip.phone.sip.handle.uac;

import com.googlecode.test.sip.phone.sip.phone.AbstractSipPhone;

public class ResponseHandlerFacade {
	
	private ByeResponseHandler byeResponseHandler;
	private RegisterResponseHandler registerResponseHandler;
	private InviteResponseHandler inviteResponseHandler;

	public ResponseHandlerFacade(AbstractSipPhone sipPhone) {
		this.byeResponseHandler=new ByeResponseHandler(sipPhone);
		this.registerResponseHandler=new RegisterResponseHandler(sipPhone);
		this.inviteResponseHandler=new InviteResponseHandler(sipPhone);
	}

	public ByeResponseHandler getByeResponseHandler() {
		return byeResponseHandler;
	}

	public RegisterResponseHandler getRegisterResponseHandler() {
		return registerResponseHandler;
	}

	public InviteResponseHandler getInviteResponseHandler() {
		return inviteResponseHandler;
	}
	
	
	

}
