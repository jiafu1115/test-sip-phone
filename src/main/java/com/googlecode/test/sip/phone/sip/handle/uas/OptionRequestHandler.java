package com.googlecode.test.sip.phone.sip.handle.uas;

import static com.googlecode.test.sip.phone.sip.SipConstants.Factorys.MESSAGE_FACTORY;

import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.googlecode.test.sip.phone.sip.phone.AbstractSipPhone;

/**
 * 
 * 
 * @author jiafu
 *
 */
public class OptionRequestHandler extends AbstractRequestHandler {

	private static final Logger LOG = Logger.getLogger(OptionRequestHandler.class);

	public OptionRequestHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
	}

	@Override
	public void handle(RequestEvent requestEvent) {
		Request request = requestEvent.getRequest();
		try {
			ServerTransaction newServerTransaction = this.sipPhone.getSipProvider()
					.getNewServerTransaction(requestEvent.getRequest());

			Response response = MESSAGE_FACTORY.createResponse(Response.OK, request);
			LOG.info(response);
			newServerTransaction.sendResponse(response);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
