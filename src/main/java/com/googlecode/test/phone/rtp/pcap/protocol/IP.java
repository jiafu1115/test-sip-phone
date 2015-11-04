package com.googlecode.test.phone.rtp.pcap.protocol;

import java.util.Arrays;

import com.googlecode.test.phone.rtp.pcap.ByteUtil;




public class IP {
	 
	public static enum PayloadType{
		TCP,UDP,UNKNOWN;
	}
	
	private byte[] identification = null;
	private int start = 0;
	private byte[] raw_data = null;
	private int fragmentOffset = -1;
	private int timeToLive = -1;
	private int headerLength = -1;
	private String version = null;
	private byte[] checksum = null;
	//private byte flags;
	private String srcAddr = null;
	private byte[] srcAddrBytes = null;
	private String destAddr = null;
	private byte[] destAddrBytes = null;
	private int totalLength = -1;
	private byte[] IPData = null;
	//TCP or UDP
	private PayloadType dataType;
	public IP(byte[] raw_data, int start)
	{
		this.raw_data = raw_data;
		this.start = start;
		
		setVersion();
		setHeaderLength();
		setTotalLength();
		
		byte b = this.raw_data[start + 9];
		int type = (int)b;
		if(type == 0x06)
			this.dataType = PayloadType.TCP;
		else if(type == 0x11)
			this.dataType = PayloadType.UDP;
		else
			this.dataType = PayloadType.UNKNOWN;
		
		setSrcAddr();
		setDestAddr();
		
	}
	
	public boolean isMoreFragment()
	{
		byte b = raw_data[start + 6];
		int r = b & 0x20;
		if(r == 0)
			return false;
		else
			return true;
	}
	
	public String getVersion()
	{
 		return this.version;
	}

	private void setVersion() {
		byte b = this.raw_data[start];
		int v = (int)(b & 0xF0);
		v = (v >> 4) & 0x0F;
		version = Integer.toString((int)v);
	}
	public int getHeaderLength()
	{
 		return this.headerLength;
	}

	private void setHeaderLength() {
		byte b = this.raw_data[start];
		
		this.headerLength = (int)(b & 0x0F);
		this.headerLength = this.headerLength * 4;
	}
	 
	public int getStart()
	{
		return start;
	}
	
	public int getDataStart(){
		return start + this.headerLength;
	}
	
	public int getTimeToLive()
	{
		if(timeToLive == -1)
		{
			byte b = this.raw_data[start + 8];
			timeToLive = (int)b;
		}
		return this.timeToLive;
	}
	
	public PayloadType getDataType()
	{
 		return dataType;
	}
	
	public String getSrcAddr()
	{
 		return this.srcAddr;
	}
	public byte[] getSrcAddrBytes()
	{
		 
		return this.srcAddrBytes;
	}
	 
	private void setSrcAddr() {
		this.srcAddrBytes = new byte[4];
		try{
			System.arraycopy(this.raw_data, start+this.headerLength-8, this.srcAddrBytes, 0, 4);
		}catch(Exception e)
		{
			this.srcAddrBytes = null;
			return ;
		}
		StringBuffer str = new StringBuffer();
		str.append(Integer.toString(srcAddrBytes[0] & 0xFF));
		str.append(".");
		str.append(Integer.toString(srcAddrBytes[1] & 0xFF));
		str.append(".");
		str.append(Integer.toString(srcAddrBytes[2] & 0xFF));
		str.append(".");
		str.append(Integer.toString(srcAddrBytes[3] & 0xFF));
		this.srcAddr = str.toString();
	}
	
	private void setDestAddr()
	{
 		StringBuffer str = new StringBuffer();
		if(version.equals("4"))
		{
			this.destAddrBytes = new byte[4];
			try{
				System.arraycopy(this.raw_data, start+this.headerLength-4, this.destAddrBytes, 0, 4);
			}catch(Exception e)
			{
				this.srcAddrBytes = null;
				return ;
			}
			str.append(Integer.toString(destAddrBytes[0] & 0xFF));
			str.append(".");
			str.append(Integer.toString(destAddrBytes[1] & 0xFF));
			str.append(".");
			str.append(Integer.toString(destAddrBytes[2] & 0xFF));
			str.append(".");
			str.append(Integer.toString(destAddrBytes[3] & 0xFF));
		}else if(version.equals("6"))
		{
			this.destAddrBytes = new byte[16];
			try{
				System.arraycopy(this.raw_data, start+this.headerLength-16, this.destAddrBytes, 0, 4);
			}catch(Exception e)
			{
				this.srcAddrBytes = null;
				return ;
			}
			
			for(int i=0; i<16; i++)
			{
				str.append(Integer.toString(destAddrBytes[i] & 0xFF));
				if(i != 15)
					str.append(".");
			}
		}

		this.destAddr = str.toString();
	}
	 
	
	public byte[] getDestAddrBytes()
	{
 		return this.destAddrBytes;
	}
	
	public String getDestAddr()
	{
 		return this.destAddr;
	}

	public int getTotalLength()
	{
 		return totalLength;
	}

	private void setTotalLength() {
		byte[] buf = new byte[4];
		buf[0] = 0x00;
		buf[1] = 0x00;
		buf[2] = this.raw_data[start+2];
		buf[3] = this.raw_data[start+3];
		this.totalLength = ByteUtil.byte2Int_high(buf);
	}
	public byte[] getIdentification()
	{
		if(this.identification == null)
		{
			identification = new byte[2];
			identification[0] = this.raw_data[start+4];
			identification[0] = this.raw_data[start+5];
		}
		return this.identification;
	}
	public byte getFlags()
	{
		byte b = this.raw_data[start+6];
		int v = (int)(b & 0xF0);
		v = (v >> 5) & 0x0F;
		return (byte)v;
	}
	public int getFlagOffset()
	{
		if(this.fragmentOffset == -1)
		{
			byte b1 = this.raw_data[start+6];
			byte b2 = this.raw_data[start+7];
			fragmentOffset = (int) b1 & 0x1f;
			fragmentOffset = fragmentOffset << 8;
			fragmentOffset += (int)b2 & 0x0f;
		}
		return this.fragmentOffset;
	}
	public byte[] getHeaderChecksum()
	{
		if(this.checksum == null)
		{
			checksum = new byte[2];
			checksum[0] = this.raw_data[start+10];
			checksum[1] = this.raw_data[start+11];
		}
		return checksum;
	}
	public byte[] getIPData()
	{
		if(this.IPData == null)
		{
			this.getHeaderLength();
			int data_start = this.start + this.headerLength;
			int data_end = this.start + this.totalLength;
			int len = data_end - data_start;
			if(len > 0)
			{
				this.IPData = new byte[len];
				try{
					System.arraycopy(raw_data, data_start, IPData, 0, len);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}else
			{
				IPData = new byte[0];
			}
		}
		return IPData;
	}

	@Override
	public String toString() {
		return "IP [srcAddr=" + srcAddr + ", destAddr=" + destAddr
				+ ", totalLength=" + totalLength + ", headerLength="
				+ headerLength + ", dataType=" + dataType + "]";
	}

	 
	
	
}
