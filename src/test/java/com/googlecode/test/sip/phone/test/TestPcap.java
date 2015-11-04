package com.googlecode.test.sip.phone.test;

import com.googlecode.test.phone.rtp.pcap.PCAPPackageParser;
import com.googlecode.test.phone.rtp.pcap.Package;

public class TestPcap {
	
	public static void main(String[] args) {
		PCAPPackageParser parser = new PCAPPackageParser("SIP_CALL_RTP_G711.cap");
		
		Package nextPackage = parser.getNextPackage();
		
		System.out.println(nextPackage.getPackageHeader());
		System.out.println(nextPackage.getPackageData());

		
		while((nextPackage = parser.getNextPackage())!=null){
			System.out.println(nextPackage.getPackageData());
		}
	/*	
		Magic：4B：0x1A 2B 3C 4D:用来标示文件的开始
		Major：2B，0×02 00:当前文件主要的版本号
		Minor：2B，0×04 00当前文件次要的版本号
		ThisZone：4B当地的标准时间；全零
		SigFigs：4B时间戳的精度；全零
		SnapLen：4B最大的存储长度
		LinkType：4B链路类型
		*/
							
		//[-44, -61, -78, -95, 2, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 0, 0, 1, 0, 0, 0, 42]
 
  		parser.close();
	}

}
