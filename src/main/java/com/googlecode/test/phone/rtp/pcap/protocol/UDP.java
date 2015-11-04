package com.googlecode.test.phone.rtp.pcap.protocol;


 
//0-1:source_port
//2-3:dest_port
//4-5:length  
//6-7:checksum
 
public class UDP {
	
	private int source_port;
	private int dest_port;
	private int length;
 	private byte[] checksum;
	private byte[] udpData;
	 
	private int start;
	private byte[] raw_data;
	private int dataLength;
 
	public UDP(byte[] raw_data, int start)
	{
		this.raw_data = raw_data;
		this.start = start;
		
		//basic headers;
		setSourcePort();
		setDestPort();
		setLength();
		setChecksum();
		
		//data part
		setDataLength(this.length);
		setUdpdata(this.dataLength);
	}
	
 	public int getDataLength()
	{
 		return this.dataLength;
	}

	private void setDataLength(int length) {
		this.dataLength = length - 8;
		if(this.dataLength < 0)
			this.dataLength = 0;
	}
	
 	public int getDataStart()
	{
 		return start + 8;
	}
	public int getSourcePort()
	{
 		return this.source_port;
	}

	private void setSourcePort() {
		source_port = raw_data[start+1] & 0xFF;
		source_port |= ((raw_data[start] << 8) & 0xFF00);
	}
	
	public int getDestPort()
	{
 		return this.dest_port;
	}

	private void setDestPort() {
		dest_port = raw_data[start+3] & 0xFF;
		dest_port |= ((raw_data[start+2] << 8) & 0xFF00);
	}
	
	
 	public int getLength()
	{
 		return this.length;
	}

	private void setLength() {
		byte low = raw_data[start+5];
		byte high = raw_data[start+4];
		length = (int)low & 0xFF;
		length |= ((int)high << 8) & 0xFF00;
	}
	
	public byte[] getChecksum()
	{
 		return this.checksum;
	}

	private void setChecksum() {
		this.checksum = new byte[2];
		checksum[0] = raw_data[start+6];
		checksum[1] = raw_data[start+7];
	}
	
	public byte[] getUDPData()
	{
 		return this.udpData;
	}

	private void setUdpdata(int dataLength) {
 		if(dataLength < 0)
		{
			this.udpData = null;
		}	
		this.udpData = new byte[dataLength];
		getDataStart();
		int aa_len = raw_data.length;
		
		if(dataLength > aa_len - this.start)
		{
			this.udpData = new byte[0];
 		}
		if(dataLength != 0 )
		{
			try{
				//FIXME  //	System.arraycopy(raw_data, this.start, udpData, 0, len);
 				System.arraycopy(raw_data, getDataStart(), this.udpData, 0, dataLength);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return "UDP [source_port=" + source_port + ", dest_port=" + dest_port
				+ ", length=" + length + ", dataLength=" + dataLength + "]";
	}
	
	
}
