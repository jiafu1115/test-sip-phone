package com.googlecode.test.phone.rtp.pcap.protocol;

import java.util.Arrays;

public class Ethernet2 {
 
	public static enum PayloadType{
 		IPV4,IPV6,UNKNOWN;
	}
	
 	private byte raw_data[] = null;
	private int start;
	
 	private byte[] macSource = null;
	private byte[] macDest = null;
  	private PayloadType macDataType;
	public Ethernet2(byte []raw_data, int start)
	{
		this.raw_data = raw_data;
		this.start = start;
		
		this.macSource = new byte[6];
 		System.arraycopy(raw_data, start+6, macSource, 0, 6);
 		
 		this.macDest = new byte[6];
 		System.arraycopy(raw_data, start, macDest, 0, 6);
		
 		int b1 = (int)this.raw_data[12];
		int b2 = (int)this.raw_data[13];
		if(b1 == 0x08 && b2 == 0x00)
			this.macDataType = PayloadType.IPV4;
		else if(b1 == 0x86 && b2 == 0xDD)
			this.macDataType = PayloadType.IPV6;
		else
			this.macDataType = PayloadType.UNKNOWN;
 	}
	
	public byte[] getMacSrcAddr()
	{
 		return this.macSource;
	}
	
	public byte[] getMacDestAddr()
	{
 		return this.macDest;
	}
	
	public int getDataStart()
	{
		return start + 14;
	}
	
	public PayloadType getMacDataType()
	{
 		return this.macDataType;
	}

	@Override
	public String toString() {
		return "Ethernet2 [macSource=" + Arrays.toString(macSource) + ", macDest=" + Arrays.toString(macDest)
				+ ", macDataType=" + macDataType + "]";
	}
	
	
	
}
