package com.googlecode.test.phone.sip.handle.uac;

import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;

import java.util.Set;

import javax.sdp.SessionDescription;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ResponseEvent;
import javax.sip.address.Address;
import javax.sip.header.CSeqHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.AbstractSipPhone;
import com.googlecode.test.phone.ReferFuture;
import com.googlecode.test.phone.rtp.codec.AudioCodec;
import com.googlecode.test.phone.sip.SipConstants;
import com.googlecode.test.phone.sip.sdp.SdpInfo;
import com.googlecode.test.phone.sip.sdp.SdpUtil;

public class InviteResponseHandler extends AbstractResponseHandler {

	private static final Logger LOG = Logger.getLogger(InviteResponseHandler.class);

	public InviteResponseHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
	}

	@Override
	public void handle(ResponseEvent arg0) {
		
		Response response = arg0.getResponse();
		ClientTransaction clientTransaction = arg0.getClientTransaction();
		Dialog dialog = clientTransaction.getDialog();
	 

		try {
 			if (arg0.getResponse().getStatusCode() == SIPResponse.MOVED_TEMPORARILY) {
				
				LOG.info("RECEVIED 302 FOR INVITE");
				
 	            Request request = (Request)arg0.getClientTransaction().getRequest().clone();
				//update seqnumber
                long seqNumber = (((CSeqHeader) request
                        .getHeader(CSeqHeader.NAME)).getSeqNumber());
                
                request.setHeader(SipConstants.Factorys.HEADER_FACTORY
                        .createCSeqHeader(++seqNumber, SIPRequest.INVITE));
                
                //add route header
                Address address = ((ContactHeader)response.getHeader(ContactHeader.NAME)).getAddress();
				RouteHeader routeHeader =
                		SipConstants.Factorys.HEADER_FACTORY.createRouteHeader(address);
                request.addHeader(routeHeader);
                
                //set via header by update branch id
                ViaHeader viaHeader = (ViaHeader)request.getHeader(ViaHeader.NAME);
                viaHeader.setBranch(Utils.getInstance().generateBranchId());
                request.setHeader(viaHeader);
                
				LOG.info("302 redirect to :"+address);
				LOG.info(request);
 
                
                ClientTransaction newClientTransaction = this.sipPhone.getSipProvider().getNewClientTransaction(request);
                
      	 		this.sipPhone.setDialog(newClientTransaction.getDialog());
      	 		
                newClientTransaction.sendRequest();
  
			}
 
			if (arg0.getResponse().getStatusCode() == SIPResponse.OK) {
				
				ReferFuture referFuture = this.sipPhone.getReferFuture();
				
				
				System.err.println("xinxiu---------------"+ referFuture);
				if(referFuture!=null){
					referFuture.setResult(SIPResponse.OK);
				}
				
				Request request = dialog.createAck(1);
 
				SdpInfo sdpMedia = SdpUtil.parseAudioCodecFromSdpContent((byte[]) response.getContent());
				Set<AudioCodec> remoteSupportedAudioCodecs = sdpMedia.getCodec();

				Set<AudioCodec> negotiationCodec = remoteSupportedAudioCodecs;
				if (!this.sipPhone.isEarlyOffer()) {
					negotiationCodec = SdpUtil.negotiationCodec(remoteSupportedAudioCodecs,
							this.sipPhone.getSupportAudioCodec());
					SessionDescription sessionDescription = SdpUtil.createSessionDescription(sipPhone.getLocalIp(),
							sipPhone.getLocalRtpPort(), negotiationCodec);
					request.setContent(sessionDescription,
							SipConstants.Factorys.HEADER_FACTORY.createContentTypeHeader("application", "sdp"));
				}

				dialog.sendAck(request);

				LOG.info(request);

				this.sipPhone.setRtpSession(dialog.getDialogId(),sipPhone.getLocalIp(), sipPhone.getLocalRtpPort(), sdpMedia.getIp(),
						sdpMedia.getPort(), negotiationCodec);
				this.sipPhone.getRtpSession().start();
			}else if(arg0.getResponse().getStatusCode()>=400){
				
 				ReferFuture referFuture = this.sipPhone.getReferFuture();
				if(referFuture!=null){
					referFuture.setResult(arg0.getResponse().getStatusCode());
				}
 			}
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	 
}
