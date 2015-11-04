package com.googlecode.test.phone.rtp.pcap;




/**
 4B Timestamp��ʱ�����λ����ȷ��seconds     
 4B Timestamp��ʱ�����λ����ȷ��microseconds
 4B Caplen����ǰ�������ĳ��ȣ���ץȡ��������֡���ȣ��ɴ˿��Եõ���һ������֡��λ�á�
 4B Len���������ݳ��ȣ�������ʵ������֡�ĳ��ȣ�һ�㲻����caplen����������º�Caplen��ֵ���
 * @author jiafu
 *
 */
public class PackageHeader {
	 
	private long highTime;
	private long lowTime;
	private long capLen;
	private long len;
 	 
	public PackageHeader(byte[] rawHeader) {
		super();
		this.highTime = ByteUtil.pcapBytesToLong(rawHeader, 0)*1000;
		this.lowTime = ByteUtil.pcapBytesToLong(rawHeader, 4)/1000;
 		this.capLen = ByteUtil.pcapBytesToLong(rawHeader, 8);
 		this.len = ByteUtil.pcapBytesToLong(rawHeader, 12);
 	}
 
	
	public long getCapLen()
	{
  		return capLen;
	}
	
	public long getTime(){
		return highTime + lowTime;
	}
	
	public long getLen()
	{
 		return len;
	}


	@Override
	public String toString() {
		return "PackageHeader [highTime=" + highTime + ", lowTime=" + lowTime + ", capLen=" + capLen + ", len=" + len
				+ "]";
	}
	
	
}
