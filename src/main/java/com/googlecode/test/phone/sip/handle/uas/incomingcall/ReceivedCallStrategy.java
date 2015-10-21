package com.googlecode.test.phone.sip.handle.uas.incomingcall;

import javax.sip.RequestEvent;

import com.googlecode.test.phone.AbstractSipPhone;

public abstract class ReceivedCallStrategy {
	
	
	protected AbstractSipPhone abstractSipPhone;
	
	protected ReceivedCallStrategy(AbstractSipPhone abstractSipPhone){
		this.abstractSipPhone=abstractSipPhone;
	}
	
	public abstract void handle(RequestEvent requestEvent);

}
