package com.googlecode.test.sip.phone.sip.handle.uas;

import java.text.ParseException;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.ReferToHeader;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

 



import org.apache.log4j.Logger;

import com.googlecode.test.sip.phone.sip.SipConstants;
import com.googlecode.test.sip.phone.sip.phone.AbstractSipPhone;

 
/**
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
 		 
        SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        Request refer = requestEvent.getRequest();

            LOG.info("referee: got an REFER sending Accepted");
            LOG.info("referee:  " + refer.getMethod() );
            LOG.info("referee : dialog = " + requestEvent.getDialog());

            // Check that it has a Refer-To, if not bad request
            ReferToHeader refTo = (ReferToHeader) refer.getHeader( ReferToHeader.NAME );
            if (refTo==null) {
                Response bad;
		 
					bad = SipConstants.Factorys.MESSAGE_FACTORY.createResponse(Response.BAD_REQUEST, refer);
		            bad.setReasonPhrase( "Missing Refer-To" );
		            sipProvider.sendResponse( bad );
				  
                return;
            }

            
            // Always create a ServerTransaction, best as early as possible in the code
            Response response = null;
            ServerTransaction st = requestEvent.getServerTransaction();
            if (st == null) {
                st = sipProvider.getNewServerTransaction(refer);
            }

            // Check if it is an initial SUBSCRIBE or a refresh / unsubscribe
            String toTag = Integer.toHexString( (int) (Math.random() * Integer.MAX_VALUE) );
            response = SipConstants.Factorys.MESSAGE_FACTORY.createResponse(202, refer);
            ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
            
            dialog = st.getDialog();

            // Sanity check: to header should not have a tag. Else the dialog
            // should have matched
            if (toHeader.getTag()!=null) {
                System.err.println( "####ERROR: To-tag!=null but no dialog match! My dialog=" + dialog.getState() );
            }
            toHeader.setTag(toTag); // Application is supposed to set.

            // REFER dialogs do not terminate on bye.
            dialog.terminateOnBye(false);
            if (dialog != null) {
                LOG.info("Dialog " + dialog);
                LOG.info("Dialog state " + dialog.getState());
                LOG.info( "local tag=" + dialog.getLocalTag() );
                LOG.info( "remote tag=" + dialog.getRemoteTag() );
            }
             
            // Both 2xx response to SUBSCRIBE and NOTIFY need a Contact
            ContactHeader contactHeader = SipConstants.Factorys.HEADER_FACTORY.createContactHeader(SipConstants.Factorys.ADDRESS_FACTORY.createAddress(this.sipPhone.getLocalSipUri()));
            response.addHeader(contactHeader);

            // Expires header is mandatory in 2xx responses to REFER
            ExpiresHeader expires = (ExpiresHeader) refer.getHeader( ExpiresHeader.NAME );
            if (expires==null) {
                expires = SipConstants.Factorys.HEADER_FACTORY.createExpiresHeader(30);// rather short
            }
            response.addHeader( expires );

            /*
             * JvB: The REFER MUST be answered first.
             */
            st.sendResponse(response);

            // NOTIFY MUST have "refer" event, possibly with id
            eventHeader = SipConstants.Factorys.HEADER_FACTORY.createEventHeader("refer");

            // Not necessary, but allowed: id == cseq of REFER
            long id = ((CSeqHeader) refer.getHeader("CSeq")).getSeqNumber();
            eventHeader.setEventId( Long.toString(id) );

            sendNotify( Response.TRYING, "Trying" );

            // Then call the refer-to
           // sendInvite( refTo );
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
            ClientTransaction ct2 = this.sipPhone.getSipProvider().getNewClientTransaction(notifyRequest);

            ContentTypeHeader ct = SipConstants.Factorys.HEADER_FACTORY.createContentTypeHeader("message","sipfrag");
            ct.setParameter( "version", "2.0" );

            notifyRequest.setContent( "SIP/2.0 " + code + ' ' + reason, ct );

            // Let the other side know that the tx is pending acceptance
            //
            dialog.sendRequest(ct2);
            LOG.info("NOTIFY Branch ID " +
                ((ViaHeader)notifyRequest.getHeader(ViaHeader.NAME)).getParameter("branch"));
            LOG.info("Dialog " + dialog);
            LOG.info("Dialog state after NOTIFY: " + dialog.getState());
    }

}
