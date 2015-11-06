package com.googlecode.test.sip.phone.test;

import com.googlecode.test.phone.rtp.pcap.PCAPParser;
import com.googlecode.test.phone.rtp.pcap.PCAPPackage;
import com.googlecode.test.phone.rtp.pcap.protocol.IP.PayloadType;

public class TestPcap {
	
	public static void main(String[] args) {
		PCAPParser parser = new PCAPParser("G711.cap");
		
		PCAPPackage nextPackage = parser.getNextPackage();
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
		} 
		 
  
  		parser.close();
	}

}
