package com.googlecode.test.phone.rtp.pcap.protocol;

import com.googlecode.test.phone.rtp.pcap.ByteUtil;
 
public class TCP {
	private int source_port = -1;
	private int dest_port = -1;
	private int sequenceNum = -1;
	private int ackNum = -1;
	private int headerLength = -1;
	private int dataLength = -1;
	private byte flag;
	private int windowSize = -1;
	private byte checksum[] = null;
	private int dataStart = -1;
	private int tcp_len = -1;
	private int start;
	private byte[] raw_data;
	private byte[] options = null;
	private byte[] tcpData = null;
	public int getStart()
	{
		return this.start;
	}
	public TCP(byte[] raw_data, int start, int tcp_len)
	{
		this.raw_data = raw_data;
		this.start = start;
		this.tcp_len = tcp_len;
		
		setSourcePort();
		setDestPort();
		setWindowsSize();
		setHeaderLength();
		setSequenceNumber();
		setChecksum();
		setAckNumber();
		setFlags();
		
		setDataStart(this.headerLength);
		setOptions(this.headerLength);
		setDataLength(this.headerLength);
		setTcpData(this.headerLength,this.dataLength);
  	}
	
	public int getDataLength()
	{
 		return this.dataLength;
	}
	private void setDataLength(int headerLength) {
		this.dataLength = tcp_len - headerLength;
	}
	
	public int getDataStart()
	{
 		return dataStart;
	}
	private void setDataStart(int headerLength) {
 		dataStart = start + headerLength;
		if(dataStart > this.raw_data.length)
			dataStart = raw_data.length-1;
	}
 	
	public int getHeaderLength()
	{
 		return this.headerLength;
	}
	private void setHeaderLength() {
		byte len = raw_data[start+12];
		headerLength = (int)len & 0xFF;
		headerLength = ((int)headerLength >> 4) & 0x0F;
		headerLength = headerLength*4;
	}
	
	public int getSequenceNumber()
	{
 		return this.sequenceNum;
	}
	private void setSequenceNumber() {
		byte[] b = new byte[4];
		b[0] = raw_data[start+4];
		b[1] = raw_data[start+5];
		b[2] = raw_data[start+6];
		b[3] = raw_data[start+7];
		this.sequenceNum = (int)ByteUtil.pcapBytesToLong(b, 0);
	}
	public int getAckNumber()
	{
 		return this.ackNum;
	}
	private void setAckNumber() {
		byte[] b = new byte[4];
		b[0] = raw_data[start + 8];
		b[1] = raw_data[start + 9];
		b[2] = raw_data[start + 10];
		b[3] = raw_data[start + 11];
		this.ackNum = (int)ByteUtil.pcapBytesToLong(b, 0);
	}
	public byte getFlags()
	{
		return this.flag;
	}
	private void setFlags() {
		this.flag = raw_data[start + 13];
	}
	public int getWindowSize()
	{
 		return this.windowSize;
	}
	private void setWindowsSize() {
		windowSize = raw_data[start+15] & 0xFF;
		windowSize |= ((raw_data[start+14] << 8) & 0xFF00);
	}
	public byte[] getChecksum()
	{
 		return this.checksum;
	}
	private void setChecksum() {
		checksum = new byte[2];
		checksum[0] = raw_data[start + 16];
		checksum[1] = raw_data[start + 17];
	}
	public int getSourcePort()
	{
 		return source_port;
	}
	private void setSourcePort() {
		source_port = raw_data[start+1] & 0xFF;
		source_port |= ((raw_data[start] << 8) & 0xFF00);
	}
	public int getDestPort()
	{
 		return dest_port;
	}
	private void setDestPort() {
		dest_port = raw_data[start+3] & 0xFF;
		dest_port |= ((raw_data[start+2] << 8) & 0xFF00);
	}
	public byte[] getOptions()
	{
 		return this.options;
	}
	private void setOptions(int headerLength) {
 		this.options = new byte[8];
		for(int i=0; i<8; i++)
		{
			options[i] = raw_data[start + headerLength - 8 + i];
		}
	}
	public byte[] getTCPData()
	{
 		return tcpData;
	}
	private void setTcpData(int headerLength, int dataLength) {
		int data_start = this.start + this.headerLength;
		int data_end = this.start + this.dataLength;
		if(data_end > data_start)
		{
			int len = data_end - data_start;
			this.tcpData = new byte[len];
			try{
				System.arraycopy(raw_data, data_start, tcpData, 0, len);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			this.tcpData = new byte[0];
		}
	}
}
