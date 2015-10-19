package com.googlecode.test.sip.phone.sip.handle.uas;

import static com.googlecode.test.sip.phone.sip.SipConstants.Factorys.MESSAGE_FACTORY;

import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.header.CallIdHeader;
import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.googlecode.test.sip.phone.sip.phone.AbstractSipPhone;

public class ByeRequestHandler extends AbstractRequestHandler {
	
	private static final Logger LOG = Logger.getLogger(ByeRequestHandler.class);


	public ByeRequestHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
 	}

	@Override
	public void handle(RequestEvent requestEvent) {
 		Request request = requestEvent.getRequest();
 		try {
 			ServerTransaction serverTransaction = requestEvent.getServerTransaction();
  			Header alsoHeader = request.getHeader("Also");
  			
 			if (serverTransaction != null) {
					Response response = MESSAGE_FACTORY.createResponse(Response.OK, request);
					LOG.info(response);
					serverTransaction.sendResponse(response);
	  		}
 			
 			if(alsoHeader!=null){
 				CallIdHeader callIdHeader=(CallIdHeader)request.getHeader(CallIdHeader.NAME);
 				this.sipPhone.stopRtpSession();
				this.sipPhone.invite(alsoHeader.toString(), callIdHeader.getCallId()+"_also");
				 				
 			}else{
  				sipPhone.stopRtpSession();
 			}
 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
			 
 
	}

}
