package com.googlecode.test.sip.phone.sip.handle.uac;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.sip.ClientTransaction;
import javax.sip.ListeningPoint;
import javax.sip.ResponseEvent;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.googlecode.test.sip.phone.sip.SipConstants;
import com.googlecode.test.sip.phone.sip.phone.AbstractSipPhone;
import com.googlecode.test.sip.phone.sip.phone.PhoneExtension;
import com.googlecode.test.sip.phone.sip.phone.RegisteredSipPhone;
import com.googlecode.test.sip.phone.sip.util.RegisterUtil;

import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.message.SIPRequest;

public class RegisterResponseHandler extends AbstractResponseHandler {

	private static final Logger LOG = Logger.getLogger(RegisterResponseHandler.class);

	public RegisterResponseHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
	}

	@Override
	public void handle(ResponseEvent arg0) {
 		Response response = arg0.getResponse();
		if(response.getStatusCode()==401){
			handle401(arg0);
 		}else if(response.getStatusCode()==200){
			RegisteredSipPhone registeredSipPhone = (RegisteredSipPhone) (this.sipPhone);
			ReentrantLock lock = registeredSipPhone.getLock();
			lock.lock();
			ExpiresHeader  header = (ExpiresHeader)response.getHeader(SIPHeader.EXPIRES);
 			if(header!=null&&header.getExpires()==0)
	 			registeredSipPhone.getUnregisterCondition().signal();
			else{
	 			registeredSipPhone.getRegisterCondition().signal();
 			}
 			lock.unlock();
   		}
 	}

	private void handle401(ResponseEvent arg0) {
		Response response = arg0.getResponse();
		
		Request srcrequest = arg0.getClientTransaction().getRequest();
		
		ContactHeader sourceContactHeader=(ContactHeader)srcrequest.getHeader(SIPHeader.CONTACT);
		
		String parameter = sourceContactHeader.getParameter(SIPHeader.EXPIRES);
 
		try {

			LOG.info(response);

			RegisteredSipPhone registeredSipPhone = (RegisteredSipPhone) (this.sipPhone);
 			WWWAuthenticateHeader wwwAuthHeader =(WWWAuthenticateHeader) response.getHeader(SIPHeader.WWW_AUTHENTICATE);
  			 
	  		CSeqHeader cSeqHeader = SipConstants.Factorys.HEADER_FACTORY.createCSeqHeader(((CSeqHeader) response.getHeader(CSeqHeader.NAME)).getSeqNumber()+1l, SIPRequest.REGISTER); 		
 
			PhoneExtension phoneExtension = registeredSipPhone.getPhoneExtension();

			SipURI requestURI = SipConstants.Factorys.ADDRESS_FACTORY.createSipURI(null, phoneExtension.getDomain());
			Address localSipAddress = SipConstants.Factorys.ADDRESS_FACTORY
					.createAddress(sipPhone.getLocalSipUri());
 			FromHeader fromHeader = SipConstants.Factorys.HEADER_FACTORY.createFromHeader(localSipAddress, Utils.getInstance().generateTag());
			ToHeader toHeader = SipConstants.Factorys.HEADER_FACTORY.createToHeader(localSipAddress, null);
 		 
			List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
			ViaHeader viaHeader = SipConstants.Factorys.HEADER_FACTORY.createViaHeader(sipPhone.getLocalIp(),
					sipPhone.getLocalSipPort(), ListeningPoint.UDP, Utils.getInstance().generateTag());
			viaHeaders.add(viaHeader);

			ContactHeader createContactHeader = SipConstants.Factorys.HEADER_FACTORY
					.createContactHeader(localSipAddress);
			createContactHeader.setParameter(SIPHeader.EXPIRES, parameter);

			CallIdHeader header = (CallIdHeader)response.getHeader(SIPHeader.CALL_ID);
			
 			Request request = SipConstants.Factorys.MESSAGE_FACTORY.createRequest(requestURI, SIPRequest.REGISTER,
					header, cSeqHeader, fromHeader, toHeader, viaHeaders,
					SipConstants.DefaultHeaders.DEFAULT_MAXFORWARDS_HEADER);

			AuthorizationHeader createAuthorizationHeader = RegisterUtil.createResponseAuthHeaderStr(phoneExtension, wwwAuthHeader);
 			request.addHeader(createAuthorizationHeader);
 			request.addHeader(createContactHeader);

			ClientTransaction newClientTransaction = sipPhone.getSipProvider().getNewClientTransaction(request);

			LOG.info(request);

			newClientTransaction.sendRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}

}
