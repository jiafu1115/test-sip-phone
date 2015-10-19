package com.googlecode.test.sip.phone.sip.handle.uas;

import javax.sip.RequestEvent;

import org.apache.log4j.Logger;

import com.googlecode.test.sip.phone.sip.phone.AbstractSipPhone;
import com.googlecode.test.sip.phone.sip.sdp.AudioSdpMedia;
import com.googlecode.test.sip.phone.sip.sdp.AudioSdpUtil;

public class AckRequestHandler extends AbstractRequestHandler {
	
	private static final Logger LOG = Logger.getLogger(AckRequestHandler.class);


	public AckRequestHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
 	}

	@Override
	public void handle(RequestEvent requestEvent) {
   			try {
				Object content = requestEvent.getRequest().getContent();
				if(content!=null)
				{
			  		AudioSdpMedia sdpMedia = AudioSdpUtil.parseAudioCodecFromSdpStr((byte[])content);
					this.sipPhone.setRtpSession(sipPhone.getLocalIp(),sipPhone.getLocalRtpPort(), sdpMedia.getIp(),
							sdpMedia.getPort(), sdpMedia.getCodec());
					this.sipPhone.getRtpSession().start();
				}else{
					if(this.sipPhone.getRtpSession()!=null)
						this.sipPhone.getRtpSession().start();

				}

			} catch (Exception ex) {
			ex.printStackTrace();
		}
			 
 
	}

}
