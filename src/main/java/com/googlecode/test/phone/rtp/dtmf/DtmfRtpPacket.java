package com.googlecode.test.phone.rtp.dtmf;

import com.googlecode.test.phone.rtp.RtpPacket;

public class DtmfRtpPacket extends RtpPacket{
	
	/**
	 * The first DtmfRtpPacket for the same DTMF event.
	 */
	private DtmfRtpPacket firstDtmfRtpPacket;

	public DtmfRtpPacket() {
		super();
 	}
	
 	
	public DtmfRtpPacket(DtmfRtpPacket firstDtmfRtpPacket) {
		super();
		this.firstDtmfRtpPacket = firstDtmfRtpPacket;
	}


	public DtmfRtpPacket getFirstDtmfRtpPacket() {
		return firstDtmfRtpPacket;
	}


	public void setFirstDtmfRtpPacket(DtmfRtpPacket dtmfRtpPacket) {
		this.firstDtmfRtpPacket = dtmfRtpPacket;
	}
  
	
}
