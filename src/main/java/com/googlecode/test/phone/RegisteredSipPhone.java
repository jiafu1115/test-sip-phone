package com.googlecode.test.phone;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.sip.ClientTransaction;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.SipException;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.sip.SipConstants;

import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.header.ParametersHeader;
import gov.nist.javax.sip.message.SIPRequest;

public class RegisteredSipPhone extends AbstractSipPhone {

	private static Logger LOG = Logger.getLogger(RegisteredSipPhone.class);

	private PhoneExtension phoneExtension;
	private int registerTime = 200;
 	private Object registerWaitObject=new Object();
 	
 	private ReentrantLock lock=new ReentrantLock();
 	private Condition registerCondition=lock.newCondition();
 	private Condition unregisterCondition=lock.newCondition();
  
  
	public ReentrantLock getLock() {
		return lock;
	}

	public Condition getRegisterCondition() {
		return registerCondition;
	}

	public Condition getUnregisterCondition() {
		return unregisterCondition;
	}
 
	public int getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(int registerTime) {
		this.registerTime = registerTime;
	}

	public RegisteredSipPhone(PhoneExtension phoneExtension) {
		super();
		this.phoneExtension=phoneExtension;
		this.localSipUri=getLocalSipUrl(phoneExtension.getUser());
  		register(); 
 	}
	
	public void test(){
		System.err.println("fujian");
		
	}

	public PhoneExtension getPhoneExtension() {
		return phoneExtension;
	}

	public void setPhoneExtension(PhoneExtension phoneExtension) {
		this.phoneExtension = phoneExtension;
	}

	/**
	 * 
	 * [CDATA[SIP/2.0 401 Unauthorized Via: SIP/2.0/UDP
	 * 10.140.202.91:59592;branch=z9hG4bKwvsIkjMsIu;received=10.140.202.91 From:
	 * <sip:3001@10.224.2.213>;tag=YcpUldfvCx To:
	 * <sip:3001@10.224.2.213>;tag=as0337284f Call-ID:
	 * bbd973c1537a83587163bd13e5273e06@10.140.202.91 CSeq: 1 REGISTER
	 * User-Agent: Asterisk PBX Allow:
	 * INVITE,ACK,CANCEL,OPTIONS,BYE,REFER,SUBSCRIBE,NOTIFY Contact:
	 * <sip:3001@10.224.2.213> WWW-Authenticate: Digest
	 * algorithm=MD5,realm="asterisk",nonce="2e30c468" Content-Length: 0
	 * 
	 * ]]>
	 * 
	 */

	
	private void register(int regestTime) {
		try {
			SipURI requestURI = SipConstants.Factorys.ADDRESS_FACTORY.createSipURI(null, phoneExtension.getDomain());
			Address localSipAddress = SipConstants.Factorys.ADDRESS_FACTORY.createAddress(requestURI);
 			
			FromHeader fromHeader = SipConstants.Factorys.HEADER_FACTORY.createFromHeader(localSipAddress, Utils.getInstance().generateTag());
			ToHeader toHeader = SipConstants.Factorys.HEADER_FACTORY.createToHeader(localSipAddress, null);
			CSeqHeader cSeqHeader = SipConstants.Factorys.HEADER_FACTORY.createCSeqHeader(1l, SIPRequest.REGISTER);
			CallIdHeader callIdHeader =this.sipProvider.getNewCallId();
 			List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
			ViaHeader viaHeader = SipConstants.Factorys.HEADER_FACTORY.createViaHeader(localIp, localSipPort,
					ListeningPoint.UDP, Utils.getInstance().generateBranchId());
			viaHeaders.add(viaHeader);

			ContactHeader createContactHeader = SipConstants.Factorys.HEADER_FACTORY
					.createContactHeader(localSipAddress);
			createContactHeader.setParameter(ParametersHeader.EXPIRES, String.valueOf(regestTime + ""));

			Request request = SipConstants.Factorys.MESSAGE_FACTORY.createRequest(requestURI, SIPRequest.REGISTER,
					callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders,
					SipConstants.DefaultHeaders.DEFAULT_MAXFORWARDS_HEADER);

			request.addHeader(createContactHeader);

			ClientTransaction newClientTransaction = this.sipProvider.getNewClientTransaction(request);

			LOG.info(request);

			newClientTransaction.sendRequest();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
 			e.printStackTrace();
		} catch (SipException e) {
 			e.printStackTrace();
		}

	}
  
	public Object getRegisterWaitObject() {
		return registerWaitObject;
	}

	public void register() {
 		lock.lock();
		try {
			register(this.registerTime);
 			registerCondition.await();
		} catch (InterruptedException e) {
 			e.printStackTrace();
		}
		lock.unlock();
 		LOG.info("[sip] complete register");
		
	}

	public void unregister() {
  		lock.lock();
		try {
			register(0);
			unregisterCondition.await();
		} catch (InterruptedException e) {
 			e.printStackTrace();
		}
		lock.unlock();
 		LOG.info("[sip] complete unregister");

	}

}
