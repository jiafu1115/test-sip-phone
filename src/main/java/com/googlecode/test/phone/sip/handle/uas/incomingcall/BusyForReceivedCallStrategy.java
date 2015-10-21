package com.googlecode.test.phone.sip.handle.uas.incomingcall;

import static com.googlecode.test.phone.sip.SipConstants.Factorys.ADDRESS_FACTORY;
import static com.googlecode.test.phone.sip.SipConstants.Factorys.HEADER_FACTORY;
import static com.googlecode.test.phone.sip.SipConstants.Factorys.MESSAGE_FACTORY;

import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.header.ContactHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.AbstractSipPhone;

public class BusyForReceivedCallStrategy extends ReceivedCallStrategy {
	
	
	private static final Logger LOGGER=Logger.getLogger(BusyForReceivedCallStrategy.class);

	
	public BusyForReceivedCallStrategy(AbstractSipPhone abstractSipPhone) {
		super(abstractSipPhone);
 	}
 
	@Override
	public void handle(RequestEvent requestEvent) {
		LOGGER.info("[SIP][ReceivedCall]: 486 busy for the call:"+requestEvent.getRequest().getRequestURI());
		
		try {

			Request request = requestEvent.getRequest();

			ServerTransaction newServerTransaction = this.abstractSipPhone.getSipProvider()
					.getNewServerTransaction(request);

			if (newServerTransaction != null) {
 				Response response = MESSAGE_FACTORY.createResponse(Response.BUSY_HERE, request);
 				ContactHeader createContactHeader = HEADER_FACTORY
						.createContactHeader(ADDRESS_FACTORY.createAddress(this.abstractSipPhone.getLocalSipUri()));
				response.addHeader(createContactHeader);

				LOGGER.info(response);
				newServerTransaction.sendResponse(response);

 			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

 	}

}
