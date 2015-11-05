package com.googlecode.test.phone.sip.handle.uas;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.ReferToHeader;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.AbstractSipPhone;
import com.googlecode.test.phone.ReferResult;
import com.googlecode.test.phone.ReferResultFuture;
import com.googlecode.test.phone.sip.SipConstants;

 
/**
 * 
 * 
 * 
  Agent A                  Agent B
      |                        |
      |   F1 REFER             |
      |----------------------->|
      |        F2 202 Accepted |
      |<-----------------------|
      |        F3 NOTIFY       |
      |<-----------------------|
      |  F4 200 OK             |
      |----------------------->|
      |                        |
      |                        |
      |                        |------->
      |                        |  (whatever)
      |                        |<------
      |                        |
      |         F5 NOTIFY      |
      |<-----------------------|
      |   F6 200 OK            |
      |----------------------->|
      |                        |
      |                        |
      
      Unattended transfer，即盲转blind transfer，Transferor与Transferee之间存在一个呼叫，但与Transfer Target之间不存在呼叫;
可能的过程：Transferor向Transferee发送REFER，Transferee回送202 Accepted;Transferee向Transferor发NOTIFY(100 Trying)，并向Transfer Target发起INVITE，在收到200 OK时使用NOTIFY(100 OK)通知Transferor，然后Transferor或Transferee任一方BYE挂断primary call;
Attended transfer，即supervised transfer，Transferor与Transferee及Transfer Target间都存在呼叫;
成功的呼转并不改变Transferor与Transferee间的媒体流，并不影响原有的session;任一方都可以在REFER后通过发送BYE消息来结束原有的session;

 * http://tools.ietf.org/html/rfc3515
 * https://tools.ietf.org/html/rfc5589
 * @author jiafu
 *
 */
public class ReferRequestHandler extends AbstractRequestHandler {
	
	private static final Logger LOG = Logger.getLogger(ReferRequestHandler.class);

	private EventHeader eventHeader;
	private Dialog dialog;
	
	public ReferRequestHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
 	}

	@Override
	public void handle(RequestEvent requestEvent) throws Exception {
 		boolean supportRefer = this.sipPhone.isSupportRefer();
		if(supportRefer){
	        refer(requestEvent);
 		}else{
 			this.sipPhone.stopRtpSession(requestEvent.getDialog().getDialogId());
 		}
 		 
    }

	private void refer(RequestEvent requestEvent) throws Exception {
			SipProvider sipProvider = (SipProvider) requestEvent.getSource();
			Request referRequest = requestEvent.getRequest();
        	
			this.dialog=requestEvent.getDialog();
            LOG.info("referee : dialog = " + dialog);

            // Check that it has a Refer-To, if not bad request
            ReferToHeader refTo = (ReferToHeader) referRequest.getHeader( ReferToHeader.NAME );
            if (refTo==null) {
                Response bad = SipConstants.Factorys.MESSAGE_FACTORY.createResponse(Response.BAD_REQUEST, referRequest);
		        bad.setReasonPhrase( "Missing Refer-To" );
		        sipProvider.sendResponse(bad );
                 
		        return;
            }

 			send202ForRequest(requestEvent, sipProvider);
 
            setEventHeader(referRequest);
            
            this.sipPhone.stopRtpSession(dialog.getDialogId());

            sendNotify(Response.TRYING, "Trying" );
            
            final ReferResultFuture referFuture = new ReferResultFuture();
			this.sipPhone.setReferFuture(referFuture); 		
			
		    this.sipPhone.invite(refTo.getAddress().getURI().toString(), getNewCallId(referRequest));
 		    
		    sendNotifyAccordingToReferFuture(referFuture);

          
	}

	private void setEventHeader(Request referRequest) throws ParseException {
		// NOTIFY MUST have "refer" event, possibly with id
		this.eventHeader = SipConstants.Factorys.HEADER_FACTORY.createEventHeader("refer");

		// Not necessary, but allowed: id == cseq of REFER
		long id = ((CSeqHeader) referRequest.getHeader("CSeq")).getSeqNumber();
		this.eventHeader.setEventId( Long.toString(id) );
	}

	private String getNewCallId(Request referRequest) {
		CallIdHeader callIdHeader=(CallIdHeader)referRequest.getHeader(CallIdHeader.NAME);
		String callId = callIdHeader.getCallId()+"_refer";
		return callId;
	}

	private void sendNotifyAccordingToReferFuture(final ReferResultFuture referFuture) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
 					try {
						ReferResult referResult = referFuture.get(5, TimeUnit.SECONDS);
 			            sendNotify(referResult.getStatusCode(),referResult.getReason());
		 			} catch (Exception e) {
		 				LOG.error(e.getMessage(),e);
			            try {
							sendNotify(503, e.getMessage());
						} catch (Exception e1) {
							e1.printStackTrace();
						}  
					} finally{
						sipPhone.setReferFuture(null);
		 			}
		    					
			}
		}).start();
	}

	private void send202ForRequest(RequestEvent requestEvent, SipProvider sipProvider) throws Exception{
 		Request referRequest = requestEvent.getRequest();
 		ServerTransaction serverTransaction = requestEvent.getServerTransaction();
		if (serverTransaction == null) {
		    serverTransaction = sipProvider.getNewServerTransaction(referRequest);
		}
		
 		Response response = generateAcceptResponse(referRequest);
		serverTransaction.sendResponse(response);
 	}

	private Response generateAcceptResponse(Request refer)
			throws ParseException, InvalidArgumentException {
		Response response = SipConstants.Factorys.MESSAGE_FACTORY.createResponse(202, refer);
		
 		ContactHeader contactHeader = SipConstants.Factorys.HEADER_FACTORY.createContactHeader(SipConstants.Factorys.ADDRESS_FACTORY.createAddress(this.sipPhone.getLocalSipUri()));
		response.addHeader(contactHeader);

		// Expires header is mandatory in 2xx responses to REFER
		ExpiresHeader expires = (ExpiresHeader) refer.getHeader( ExpiresHeader.NAME );
		if (expires==null) {
		    expires = SipConstants.Factorys.HEADER_FACTORY.createExpiresHeader(30);// rather short
		}
		response.addHeader( expires );

		LOG.info(response);
		
		return response;
		
	}

        private void sendNotify( int code, String reason )
            throws SipException, ParseException
        {
            /*
             * NOTIFY requests MUST contain a "Subscription-State" header with a
             * value of "active", "pending", or "terminated". The "active" value
             * indicates that the subscription has been accepted and has been
             * authorized (in most cases; see section 5.2.). The "pending" value
             * indicates that the subscription has been received, but that
             * policy information is insufficient to accept or deny the
             * subscription at this time. The "terminated" value indicates that
             * the subscription is not active.
             */

            Request notifyRequest = dialog.createRequest( "NOTIFY" );

            // Initial state is pending, second time we assume terminated (Expires==0)
            String state = SubscriptionStateHeader.PENDING;
            if (code>100 && code<200) {
                state = SubscriptionStateHeader.ACTIVE;
            } else if (code>=200) {
                state = SubscriptionStateHeader.TERMINATED;
            }
            
            
            SubscriptionStateHeader sstate = SipConstants.Factorys.HEADER_FACTORY.createSubscriptionStateHeader( state );
            if (state == SubscriptionStateHeader.TERMINATED) {
                sstate.setReasonCode("noresource");
            }
            notifyRequest.addHeader(sstate);
            notifyRequest.setHeader(eventHeader);

            ContactHeader contactHeader = SipConstants.Factorys.HEADER_FACTORY.createContactHeader(SipConstants.Factorys.ADDRESS_FACTORY.createAddress(this.sipPhone.getLocalSipUri()));
            notifyRequest.setHeader(contactHeader);

            ContentTypeHeader contentTypeHeader = SipConstants.Factorys.HEADER_FACTORY.createContentTypeHeader("message","sipfrag");
            contentTypeHeader.setParameter( "version", "2.0" );

            notifyRequest.setContent( "SIP/2.0 " + code + ' ' + reason, contentTypeHeader );
 
            ClientTransaction clientTransaction = this.sipPhone.getSipProvider().getNewClientTransaction(notifyRequest);
            dialog.sendRequest(clientTransaction);
            
            LOG.info(notifyRequest);
            
        	if(LOG.isDebugEnabled()){
                LOG.debug("NOTIFY Branch ID " +
                        ((ViaHeader)notifyRequest.getHeader(ViaHeader.NAME)).getParameter("branch"));
                    LOG.debug("Dialog " + dialog);
                    LOG.debug("Dialog state after NOTIFY: " + dialog.getState());
        	}


    }

}
