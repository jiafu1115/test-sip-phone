package com.googlecode.test.phone.sip.handle.uas.incomingcall;

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
import com.googlecode.test.phone.sip.sdp.SdpInfo;
import com.googlecode.test.phone.sip.sdp.SdpUtil;

public class AcceptReceivedCallStrategy extends ReceivedCallStrategy {
	
	private static final Logger LOGGER = Logger.getLogger(AcceptReceivedCallStrategy.class);
 
	public AcceptReceivedCallStrategy(AbstractSipPhone abstractSipPhone) {
		super(abstractSipPhone);
	}

 	@Override
	public void handle(RequestEvent requestEvent) {
		LOGGER.info("[SIP][ReceivedCall]: accept the call:" + requestEvent.getRequest().getRequestURI());

		try {

			Request request = requestEvent.getRequest();

			ServerTransaction newServerTransaction = this.abstractSipPhone.getSipProvider()
					.getNewServerTransaction(request);

			if (newServerTransaction != null) {

				Object content = request.getContent();

				boolean isEearlyOffer = (content != null);

				Set<AudioCodec> negotiationCodec = this.abstractSipPhone.getSupportAudioCodec();
				SdpInfo audioSdpMedia = null;

				if (isEearlyOffer) {
					audioSdpMedia = SdpUtil.parseAudioCodecFromSdpContent((byte[]) content);
					negotiationCodec = SdpUtil.negotiationCodec(audioSdpMedia.getCodec(),
							this.abstractSipPhone.getSupportAudioCodec());

				}

				SessionDescription createSessionDescription = SdpUtil.createSessionDescription(
						this.abstractSipPhone.getLocalIp(), this.abstractSipPhone.getLocalRtpPort(), negotiationCodec);

				Response response = MESSAGE_FACTORY.createResponse(Response.OK, request);
				response.setContent(createSessionDescription,
						HEADER_FACTORY.createContentTypeHeader("application", "sdp"));

				ContactHeader createContactHeader = HEADER_FACTORY
						.createContactHeader(ADDRESS_FACTORY.createAddress(this.abstractSipPhone.getLocalSipUri()));
				response.addHeader(createContactHeader);

				LOGGER.info(response);
				newServerTransaction.sendResponse(response);

				if (isEearlyOffer) {
					this.abstractSipPhone.setRtpSession(this.abstractSipPhone.getLocalIp(),
							this.abstractSipPhone.getLocalRtpPort(), audioSdpMedia.getIp(), audioSdpMedia.getPort(),
							negotiationCodec);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
