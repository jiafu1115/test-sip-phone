package com.googlecode.test.phone.rtp.listeners;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.test.phone.rtp.RtpPacket;
import com.googlecode.test.phone.rtp.codec.AudioCodec;
import com.googlecode.test.phone.rtp.dtmf.DtmfFactory;

public class CollectDtmfRtpListener implements RtpListener {

	private List<Character> dtmfChars = new ArrayList<Character>();

	public CollectDtmfRtpListener() {
	}

	@Override
	public void receivedRtpPacket(RtpPacket rtpPacket) {
		boolean isFirstRtpPacketForDtmf = rtpPacket.getPayloadType() == AudioCodec.TELEPHONE_EVENT
				.getPayloadType() && rtpPacket.isMarker();
		if (isFirstRtpPacketForDtmf) {
			byte[] data = rtpPacket.getData();
			char digitFromData = DtmfFactory.getDigitFromData(data[0]);
			dtmfChars.add(digitFromData);
		}
	}

	public List<Character> getDtmfChars() {
		return dtmfChars;
	}

	@Override
	public void close() {

	}
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName();
	}

}
