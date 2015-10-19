package com.googlecode.test.phone.sip.handle.uas;

import com.googlecode.test.phone.AbstractSipPhone;

public class RequestHandlerFacade {
	
	private InviteRequestHandler inviteRequestHandler;
	private AckRequestHandler ackRequestHandler;
	private ByeRequestHandler byeRequestHandler;
	private ReferRequestHandler referRequestHandler;
	private OptionRequestHandler optionRequestHandler;

	public RequestHandlerFacade(AbstractSipPhone sipPhone) {
		this.inviteRequestHandler=new InviteRequestHandler(sipPhone);
		this.ackRequestHandler=new AckRequestHandler(sipPhone);
		this.byeRequestHandler=new ByeRequestHandler(sipPhone);
		this.referRequestHandler=new ReferRequestHandler(sipPhone);
		this.optionRequestHandler=new OptionRequestHandler(sipPhone);
	}

	public InviteRequestHandler getInviteRequestHandler() {
		return inviteRequestHandler;
	}

	public AckRequestHandler getAckRequestHandler() {
		return ackRequestHandler;
	}

	public ByeRequestHandler getByeRequestHandler() {
		return byeRequestHandler;
	}

	public ReferRequestHandler getReferRequestHandler() {
		return referRequestHandler;
	}

	public OptionRequestHandler getOptionRequestHandler() {
		return optionRequestHandler;
	}
 
}
