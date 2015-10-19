package com.googlecode.test.sip.phone.sip.phone;

import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.message.SIPRequest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.sdp.SessionDescription;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.SipStack;
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

import com.googlecode.test.sip.phone.media.RtpSession;
import com.googlecode.test.sip.phone.media.codec.AudioCodec;
import com.googlecode.test.sip.phone.sip.SipConstants;
import com.googlecode.test.sip.phone.sip.SipListenerImpl;
import com.googlecode.test.sip.phone.sip.SipStackFactory;
import com.googlecode.test.sip.phone.sip.sdp.AudioSdpUtil;
import com.googlecode.test.sip.phone.util.NetUtil;
import com.googlecode.test.sip.phone.util.PortUtil;

public abstract class AbstractSipPhone {
	
	private static final Logger LOG=Logger.getLogger(AbstractSipPhone.class);
   
	protected String localIp;
	protected int localSipPort;
	protected int localRtpPort;
	protected SipURI localSipUri;
	protected Set<AudioCodec> supportAudioCodec=new HashSet<AudioCodec>();
	protected boolean isEarlyOffer;
	protected boolean isPlayListened;
	
	
	protected Dialog dialog;
 	
	protected SipStack sipStack;
	protected SipProvider sipProvider;   	
	protected RtpSession rtpSession;
	
  	
  	{
   		localIp=NetUtil.getLocalIp();
  	 	localSipPort=PortUtil.allocateLocalPort();
  		localRtpPort=PortUtil.allocateLocalPort();
  	 
 		isEarlyOffer=true;

		supportAudioCodec.add(AudioCodec.PCMA);
		supportAudioCodec.add(AudioCodec.PCMU);
		supportAudioCodec.add(AudioCodec.TELEPHONE_EVENT);
    
  	}
  	
   	
  	protected SipURI getLocalSipUrl(String user) {
		SipURI createSipURI=null;
		try {
			createSipURI= SipConstants.Factorys.ADDRESS_FACTORY.createSipURI(user, localIp);
			createSipURI.setPort(this.localSipPort);
 			createSipURI.setTransportParam(ListeningPoint.UDP);
		} catch (ParseException e) {
 			e.printStackTrace();
		}
		return createSipURI;
	}
  	 
  
	public boolean isEarlyOffer() {
		return isEarlyOffer;
	}

	public void setEarlyOffer(boolean isEarlyOffer) {
		this.isEarlyOffer = isEarlyOffer;
	}

	public AbstractSipPhone() {
		super();
  		sipStack = SipStackFactory.getInstance().createSipStack();
 
		try {
			ListeningPoint sipListeningPoint = sipStack.createListeningPoint(localIp, localSipPort, ListeningPoint.UDP);
			sipProvider = sipStack.createSipProvider(sipListeningPoint);
			sipProvider.addSipListener(new SipListenerImpl(this));
 		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
 		LOG.info("[sipstack]sip stack start for :"+this);
 	}
	
 

	public void enablePlayListened() {
		this.isPlayListened = true;
	}

	public SipProvider getSipProvider() {
		return sipProvider;
	}
 	
	public RtpSession getRtpSession() {
		return rtpSession;
	}
 
	public void setRtpSession(RtpSession rtpSession) {
		this.rtpSession = rtpSession;
 		if(isPlayListened)
 			this.rtpSession.enablePlay();
	}
	
	public void setRtpSession(String localIp,int localRtpPort,String remoteIp, int remoteRtpPort, Set<AudioCodec> audioCodecs){
		setRtpSession( new RtpSession(localIp,localRtpPort,remoteIp,remoteRtpPort,audioCodecs));
   	}
	 
	public SipStack getSipStack() {
		return sipStack;
	}
 
	public void sendDtmf(String dtmfs){
		sendDtmf(dtmfs,0);
 	}
 	
 	public void sendDtmf(String digits, int sleepTimeByMilliSecond){
		if(rtpSession==null)
			throw new RuntimeException("sip negotition not setup");
		rtpSession.sendDtmf(digits,sleepTimeByMilliSecond);
 	}
 	
  
	public void invite(String requestUrl,String callId) {
		try{
	 		Request request = createInviteRequestWithSipHeader(requestUrl, callId);
  	 		 
	 		if(isEarlyOffer){
	 	 		SessionDescription sessionDescription = AudioSdpUtil.createSessionDescription(localIp, localRtpPort, supportAudioCodec);
		     	request.setContent(sessionDescription, SipConstants.Factorys.HEADER_FACTORY.createContentTypeHeader("application", "sdp"));
 	 		}
	 		
 	     	LOG.info(request);
	    	
	 		ClientTransaction newClientTransaction = sipProvider.getNewClientTransaction(request);
 	 		dialog=newClientTransaction.getDialog();
 	 		 
			newClientTransaction.sendRequest();
			 
 		}catch(Exception exception){
			throw new RuntimeException(exception.getMessage(),exception);
		}

 	}
  	
 	public void bye() {
  		try {
  			if(dialog==null){
  			    this.stop();
  			    return;
  			}
 			 Request byeRequest = this.dialog.createRequest(Request.BYE);
			 LOG.info(byeRequest);
	         ClientTransaction ct = sipProvider.getNewClientTransaction(byeRequest);
	         dialog.sendRequest(ct);

		} catch (SipException e) {
 			e.printStackTrace();
		}  
  	}
  
	/**
	 *  

	 * @param requestUrl  
	 * @param callId
	 * @return
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 */
	private Request createInviteRequestWithSipHeader(String requestUrl,String callId)
			throws ParseException, InvalidArgumentException {
 		Address requestAddress = SipConstants.Factorys.ADDRESS_FACTORY.createAddress(requestUrl);
  		Address localSipAddress = SipConstants.Factorys.ADDRESS_FACTORY.createAddress(localSipUri);
   		String fromTag = Utils.getInstance().generateTag();
		FromHeader fromHeader = SipConstants.Factorys.HEADER_FACTORY.createFromHeader(localSipAddress, fromTag);
  		ToHeader toHeader = SipConstants.Factorys.HEADER_FACTORY.createToHeader(requestAddress, null);
  		CSeqHeader cSeqHeader = SipConstants.Factorys.HEADER_FACTORY.createCSeqHeader(1l, SIPRequest.INVITE); 		
  		CallIdHeader callIdHeader = SipConstants.Factorys.HEADER_FACTORY.createCallIdHeader(callId); 
  		 
 		List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
		ViaHeader viaHeader = SipConstants.Factorys.HEADER_FACTORY.createViaHeader(localIp,
				localSipPort, ListeningPoint.UDP, Utils.getInstance().generateBranchId());
		viaHeaders.add(viaHeader);
		
 		ContactHeader createContactHeader = SipConstants.Factorys.HEADER_FACTORY.createContactHeader(localSipAddress);
    		
		Request request = SipConstants.Factorys.MESSAGE_FACTORY.createRequest(requestAddress.getURI(),
				SIPRequest.INVITE, callIdHeader, cSeqHeader, fromHeader,
				toHeader, viaHeaders, SipConstants.DefaultHeaders.DEFAULT_MAXFORWARDS_HEADER);
		
 		request.addHeader(createContactHeader);
		return request;
	}

	public void stop(){
 		LOG.info("sip stack stop for:"+this);
 		
		try{
 	 		TimeUnit.MILLISECONDS.sleep(500);
  			if(rtpSession!=null)
 				rtpSession.stop();
    		} catch (InterruptedException e) {
 				e.printStackTrace();
			}finally{
      			sipStack.stop();
   		}

	} 
	
	public void stopRtpSession(){
 		LOG.info("stop rtp for:"+this);
  		if(rtpSession!=null)
 				rtpSession.stop();
 	} 

	public Set<AudioCodec> getSupportAudioCodec() {
		return supportAudioCodec;
	}

	public void setSupportAudioCodec(Set<AudioCodec> supportAudioCodec) {
		this.supportAudioCodec = supportAudioCodec;
	}

	public String getLocalIp() {
		return localIp;
	}

	public int getLocalSipPort() {
		return localSipPort;
	}

	public int getLocalRtpPort() {
		return localRtpPort;
	}
	
	public SipURI getLocalSipUri() {
		return localSipUri;
	}
	 
 	public Dialog getDialog() {
		return dialog;
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public String toString() {
		return "AbstractSipPhone [localIp=" + localIp + ", sipPort=" + localSipPort + ", rtpPort=" + localRtpPort + "]";
	}



}
