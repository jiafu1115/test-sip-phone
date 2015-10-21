package com.googlecode.test.phone.sip.handle.uas;

import javax.sip.RequestEvent;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.AbstractSipPhone;
import com.googlecode.test.phone.AbstractSipPhone.ReceivedCallHandleType;
import com.googlecode.test.phone.sip.handle.uas.incomingcall.AcceptReceivedCallStrategy;
import com.googlecode.test.phone.sip.handle.uas.incomingcall.BusyReceivedCallStrategy;
import com.googlecode.test.phone.sip.handle.uas.incomingcall.IgnoreReceivedCallStrategy;
import com.googlecode.test.phone.sip.handle.uas.incomingcall.ReceivedCallStrategy;

public class InviteRequestHandler extends AbstractRequestHandler {
  
	private static final Logger LOG = Logger.getLogger(InviteRequestHandler.class);
 
	public InviteRequestHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
 	}

	@Override
	public void handle(RequestEvent requestEvent) {
 		ReceivedCallHandleType receivedCallHandleType = this.sipPhone.getReceivedCallHandleType();
 		LOG.info("[SIP][ReceivedCall][Current strategy for received call is:]"+receivedCallHandleType);
  		ReceivedCallStrategy receivedCallStrategy;
		switch (receivedCallHandleType) {
 		case IGNORE:
			receivedCallStrategy=new IgnoreReceivedCallStrategy(sipPhone);
 			break;
 		case BUSY:
			receivedCallStrategy=new BusyReceivedCallStrategy(sipPhone);
 			break;
 		case ACCEPT:
		default:
			receivedCallStrategy=new AcceptReceivedCallStrategy(sipPhone);
 		}
 	
		receivedCallStrategy.handle(requestEvent);
 	}

}
