package com.googlecode.test.phone.sip;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipListener;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.header.CSeqHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.AbstractSipPhone;
import com.googlecode.test.phone.sip.handle.uac.ResponseHandlerFacade;
import com.googlecode.test.phone.sip.handle.uas.RequestHandlerFacade;

public class SipListenerImpl implements SipListener {

	private static final Logger LOG = Logger.getLogger(SipListenerImpl.class);

	private ResponseHandlerFacade responseHandlerFacade;
	private RequestHandlerFacade requestHandlerFacade;
	
	private ReceivedMessages receivedMessages=new ReceivedMessages();
 

	public SipListenerImpl(AbstractSipPhone sipPhone) {
		this.responseHandlerFacade = new ResponseHandlerFacade(sipPhone);
		this.requestHandlerFacade = new RequestHandlerFacade(sipPhone);
	}

	

	public ReceivedMessages getReceivedMessages() {
		return receivedMessages;
	}

 

	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {

	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
	}

	@Override
	public void processRequest(RequestEvent requestEvent) {
		Request request = requestEvent.getRequest();
		receivedMessages.getReceivedRequests().add(request);

		LOG.info(request);

		if (request.getMethod().equals(Request.REFER)) {
			try {
				requestHandlerFacade.getReferRequestHandler().handle(requestEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		if (request.getMethod().equals(Request.BYE)) {
			requestHandlerFacade.getByeRequestHandler().handle(requestEvent);
			return;
		}

		if (request.getMethod().equals(Request.INVITE)) {
			requestHandlerFacade.getInviteRequestHandler().handle(requestEvent);
			return;
		}

		if (request.getMethod().equals(Request.ACK)) {
			requestHandlerFacade.getAckRequestHandler().handle(requestEvent);
			return;
		}

		if (request.getMethod().equals(Request.OPTIONS)) {
			requestHandlerFacade.getOptionRequestHandler().handle(requestEvent);
		}
	}

	@Override
	public void processResponse(ResponseEvent responseEvent) {
		Response response = responseEvent.getResponse();
		LOG.info(response);
		receivedMessages.getReceivedResponses().add(response);

		CSeqHeader cseqHeader = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
		if (cseqHeader.getMethod().equals(Request.INVITE)) {
			responseHandlerFacade.getInviteResponseHandler().handle(responseEvent);
			return;
		}

		if (cseqHeader.getMethod().equals(Request.BYE)) {
			responseHandlerFacade.getByeResponseHandler().handle(responseEvent);
			return;
		}

		if (cseqHeader.getMethod().equals(Request.REGISTER)) {
			responseHandlerFacade.getRegisterResponseHandler().handle(responseEvent);
			return;
		}

	}

	@Override
	public void processTimeout(TimeoutEvent arg0) {

	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {

	}

}
