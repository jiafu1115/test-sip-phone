package com.googlecode.test.sip.phone.test;

import com.googlecode.test.phone.rtp.RtpPacket;
import com.googlecode.test.phone.rtp.RtpParserUtil;
import com.googlecode.test.phone.rtp.pcap.PCAPParser;
import com.googlecode.test.phone.rtp.pcap.PCAPPackage;
import com.googlecode.test.phone.rtp.pcap.protocol.IP.PayloadType;

public class TestPcap {
	
	public static void main(String[] args) {
		PCAPParser parser = new PCAPParser("G711.pcap");
		
		PCAPPackage nextRtpPackage = parser.getNextRtpPackage();
		System.out.println(nextRtpPackage);
		
		RtpPacket decode = RtpParserUtil.decode(nextRtpPackage.getPackageData().getUdp().getUDPData());
		System.err.println(decode);
		  nextRtpPackage = parser.getNextRtpPackage();

			System.out.println(nextRtpPackage);
			nextRtpPackage = parser.getNextRtpPackage();
			System.out.println(nextRtpPackage);
		
	/*	PCAPPackage nextPackage = parser.getNextPackage();
 		System.out.println(nextPackage);
 		nextPackage = parser.getNextPackage();
 		System.out.println(nextPackage);
 		nextPackage = parser.getNextPackage();
 		System.out.println(nextPackage);
 
		int i=0;
	 	while((nextPackage = parser.getNextPackage())!=null&&i<200){
	 		if(nextPackage.getPackageData().getUdp()!=null)
			System.out.println(nextPackage);
			i++;
		} */
		 
  
  		parser.close();
	}

}
