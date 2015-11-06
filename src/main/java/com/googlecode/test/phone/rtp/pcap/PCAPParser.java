package com.googlecode.test.phone.rtp.pcap;

import java.io.IOException;
import java.io.InputStream;

import com.googlecode.test.phone.rtp.pcap.PCAPPackage.PackageData;
import com.googlecode.test.phone.rtp.pcap.PCAPPackage.PackageHeader;
import com.googlecode.test.phone.rtp.pcap.protocol.UDP;

public class PCAPParser {
	
 	public int current_p = 0;
 	public int file_p = 0;
   
	private byte[] packageHeaderBuffer = new byte[24];
 
 	private InputStream inputStream = null;

 
	public PCAPParser(String filename) {
  	 	this.inputStream = this.getClass().getClassLoader().getResourceAsStream(filename);
   	}

	public void close() {
		try {
			inputStream.close();
		} catch (IOException e) {
 			e.printStackTrace();
		}
	}

	public PCAPPackage getNextPackage() {
		if (current_p == 0)
			if (!checkPCAPHeader())
				return null;
		PackageHeader header = parseHeader();
		if (header == null)
			return null;
		long capLen = header.getCapLen();
 		PackageData data = parseData(capLen);

		if (data == null)
			return null;
		PCAPPackage pack = new PCAPPackage(header,data);
		
  		return pack;
	}
	
	public PCAPPackage getNextRtpPackage() {
		PCAPPackage  pcapPackage;
		while( (pcapPackage=getNextPackage())!=null){
			UDP udp = pcapPackage.getPackageData().getUdp();
			if(udp!=null){
				byte[] udpData = udp.getUDPData();
				int b = (int)(udpData[0] & 0xff);
				b= ((b & 0xc0) >> 6);
				if(b==2){
					return pcapPackage;
				}else{
					getNextRtpPackage();
				}
			}else{
				return getNextRtpPackage();
			}
		 }
		
		return null;
	}
	
  

 	public PackageData parseData(long len) {
		int r = 0;
		int len_int = (int) len;
 
		byte[] buf = new byte[len_int];
		try {
			r = inputStream.read(buf);
			if (r > 0)
				current_p += r;

		} catch (IOException e) {
 			e.printStackTrace();
			return null;
		}
		if (r != len_int)
			return null;
		PackageData data = new PackageData(buf);
		return data;
	}

 	public PackageHeader parseHeader() {
		int r = 0;
		byte[] headerBuffer = new byte[16];
		try {
			r = inputStream.read(headerBuffer);
			if (r > 0)
				current_p += r;
		} catch (IOException e) {
 			e.printStackTrace();
			return null;
		}
		if (r != 16)
			return null;
		PackageHeader header = new PackageHeader(headerBuffer);
 		return header;
	}

 	public boolean checkPCAPHeader() {
 		int r = 0;
		try {
			r = inputStream.read(this.packageHeaderBuffer);
			if (r > 0)
				current_p += r;
 
		} catch (IOException e) {
 			e.printStackTrace();
			return false;
		}
		if (r != 24)
			return false;

		return true;
	}
}
