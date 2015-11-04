package com.googlecode.test.phone.sip.handle.uas;

import javax.sip.RequestEvent;

import com.googlecode.test.phone.AbstractSipPhone;
import com.googlecode.test.phone.sip.sdp.SdpInfo;
import com.googlecode.test.phone.sip.sdp.SdpUtil;

public class AckRequestHandler extends AbstractRequestHandler {

	public AckRequestHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
	}

	@Override
	public void handle(RequestEvent requestEvent) {
		try {
			byte[] sdpContent = ((byte[])(requestEvent.getRequest().getContent()));
			if (sdpContent != null) {
				SdpInfo sdpMedia = SdpUtil.parseAudioCodecFromSdpContent(sdpContent);
				this.sipPhone.setRtpSession(requestEvent.getDialog().getDialogId(),sipPhone.getLocalIp(), sipPhone.getLocalRtpPort(), sdpMedia.getIp(),
						sdpMedia.getPort(), sdpMedia.getCodec());
				this.sipPhone.getRtpSession().start();
			} else {
				if (this.sipPhone.getRtpSession() != null)
					this.sipPhone.getRtpSession().start();
 			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
