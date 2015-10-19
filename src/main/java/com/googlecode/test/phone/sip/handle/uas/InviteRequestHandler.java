package com.googlecode.test.phone.sip.handle.uas;

import static com.googlecode.test.phone.sip.SipConstants.Factorys.ADDRESS_FACTORY;
import static com.googlecode.test.phone.sip.SipConstants.Factorys.HEADER_FACTORY;
import static com.googlecode.test.phone.sip.SipConstants.Factorys.MESSAGE_FACTORY;

import java.util.Set;

import javax.sdp.SessionDescription;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.header.ContactHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.AbstractSipPhone;
import com.googlecode.test.phone.rtp.codec.AudioCodec;
import com.googlecode.test.phone.sip.sdp.AudioSdpMedia;
import com.googlecode.test.phone.sip.sdp.AudioSdpUtil;

public class InviteRequestHandler extends AbstractRequestHandler {
	
	private static final Logger LOG = Logger.getLogger(InviteRequestHandler.class);


	public InviteRequestHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
 	}

	@Override
	public void handle(RequestEvent requestEvent) {
  		try {
			
			Request request = requestEvent.getRequest();
			
 			ServerTransaction newServerTransaction = sipPhone.getSipProvider().getNewServerTransaction(request);

			if (newServerTransaction != null) {
				
				Object content = request.getContent();
				
				boolean isEearlyOffer=(content!=null);
				
				Set<AudioCodec> negotiationCodec=sipPhone.getSupportAudioCodec();
				AudioSdpMedia audioSdpMedia = null;
				
				
				if(isEearlyOffer)
				{
					audioSdpMedia = AudioSdpUtil.parseAudioCodecFromSdpStr((byte[])content);
					negotiationCodec = AudioSdpUtil.negotiationCodec(audioSdpMedia.getCodec(), this.sipPhone.getSupportAudioCodec());
					
				}
				
				SessionDescription createSessionDescription = AudioSdpUtil.createSessionDescription(sipPhone.getLocalIp(), sipPhone.getLocalRtpPort(), negotiationCodec);
				
				
				Response response = MESSAGE_FACTORY.createResponse(Response.OK, request);
				response.setContent(createSessionDescription,
							HEADER_FACTORY.createContentTypeHeader("application", "sdp"));
			
				ContactHeader createContactHeader = HEADER_FACTORY.createContactHeader(ADDRESS_FACTORY.createAddress(this.sipPhone.getLocalSipUri()));
				response.addHeader(createContactHeader);

				LOG.info(response);
				newServerTransaction.sendResponse(response);
				
				if(isEearlyOffer){
					this.sipPhone.setRtpSession(sipPhone.getLocalIp(),sipPhone.getLocalRtpPort(), audioSdpMedia.getIp(),
							audioSdpMedia.getPort(), negotiationCodec);
				}
  
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
 
	}

}
