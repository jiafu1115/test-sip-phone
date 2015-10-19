package com.googlecode.test.phone.sip.handle.uas;

import javax.sip.RequestEvent;

import com.googlecode.test.phone.AbstractSipPhone;
import com.googlecode.test.phone.sip.sdp.AudioSdpMedia;
import com.googlecode.test.phone.sip.sdp.AudioSdpUtil;

public class AckRequestHandler extends AbstractRequestHandler {

	public AckRequestHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
	}

	@Override
	public void handle(RequestEvent requestEvent) {
		try {
			byte[] sdpContent = ((byte[])(requestEvent.getRequest().getContent()));
			if (sdpContent != null) {
				AudioSdpMedia sdpMedia = AudioSdpUtil.parseAudioCodecFromSdpContent(sdpContent);
				this.sipPhone.setRtpSession(sipPhone.getLocalIp(), sipPhone.getLocalRtpPort(), sdpMedia.getIp(),
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
