package com.googlecode.test.phone.rtp.dtmf;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.test.phone.rtp.RtpPacket;
import com.googlecode.test.phone.rtp.codec.AudioCodec;

public class DtmfFactory {

	private static final int DTMF_PAYLOAD_TYPE = AudioCodec.TELEPHONE_EVENT
			.getPayloadType();

	/**
	 * <pre>
	 * 		Char("48") 0 
	 * 		Char("49") 1
	 * 		Char("50") 2
	 * 		  
	 * 		Char("65") A 
	 * 		Char("66") B
	 * Char("67") C
	 * 
	 * <pre>
	 * 
	 * @param digit
	 * @return
	 */
	public static byte getDataForDigit(char digit) {
		if (digit == '*') {
			return 10;
		} else if (digit == '#') {
			return 11;
		} else if (digit >= 'A' && digit <= 'D') {
			return (byte) (digit - 53);
		} else {
			return (byte) (digit - 48);
		}
	}

	public static char getDigitFromData(byte digit) {
		if (digit == 10) {
			return '*';
		} else if (digit == 11) {
			return '#';
		} else if (digit >= 12 && digit <= 15) {
			return (char) (digit + 53);
		} else {
			return (char) (digit + 48);
		}
	}

	public List<RtpPacket> createDtmfPackets(char digit) {
		List<RtpPacket> packets = new ArrayList<RtpPacket>();
		byte[] data = new byte[4];
		// RFC4733

		data[0] = getDataForDigit(digit);
		data[1] = 10; // volume 10
		// Set Duration to 160
		// duration 8 bits
		data[2] = 0;
		// duration 8 bits
		data[3] = -96;

		DtmfRtpPacket firstRtpPacket = new DtmfRtpPacket();
		firstRtpPacket.setData(data);
		firstRtpPacket.setPayloadType(DTMF_PAYLOAD_TYPE);
		firstRtpPacket.setMarker(true);
		packets.add(firstRtpPacket);

		DtmfRtpPacket rtpPacket = new DtmfRtpPacket(firstRtpPacket);
		// set duration to 320
		data = data.clone();
		data[2] = 1;
		data[3] = 64;
		rtpPacket.setData(data);
		rtpPacket.setMarker(false);
		rtpPacket.setPayloadType(101);
		packets.add(rtpPacket);

		rtpPacket = new DtmfRtpPacket(firstRtpPacket);
		// set duration to 320
		data = data.clone();
		data[2] = 1;
		data[3] = -32;
		rtpPacket.setData(data);
		rtpPacket.setMarker(false);
		rtpPacket.setPayloadType(101);
		packets.add(rtpPacket);

		data = data.clone();
		// create three end event packets
		data[1] = -0x76; // end event flag + volume set to 10
		// set Duration to 640
		data[2] = 2; // duration 8 bits
		data[3] = -128; // duration 8 bits
		for (int r = 0; r < 3; r++) {
			rtpPacket = new DtmfRtpPacket(firstRtpPacket);
			rtpPacket.setData(data);
			rtpPacket.setMarker(false);
			rtpPacket.setPayloadType(101);
			packets.add(rtpPacket);
		}

		return packets;
	}

}
