package com.googlecode.test.phone.rtp.pcap;

import com.googlecode.test.phone.rtp.pcap.protocol.Ethernet2;
import com.googlecode.test.phone.rtp.pcap.protocol.IP;
import com.googlecode.test.phone.rtp.pcap.protocol.TCP;
import com.googlecode.test.phone.rtp.pcap.protocol.UDP;

 

public class PackageData {
	
 	private Ethernet2 ethernet2;
	private IP ip;
	private TCP tcp;
	private UDP udp;

	public PackageData(byte[] rawData) {
		super();
		parse(rawData);
	}

	private void parse(byte[] raw_data) {
		this.ethernet2 = new Ethernet2(raw_data, 0);
 
		if (ethernet2.getMacDataType() == Ethernet2.PayloadType.IPV4) {
			IP ipv4 = new IP(raw_data, ethernet2.getDataStart());
			this.ip = ipv4;
			if (ipv4.getDataType() == IP.PayloadType.UDP) {
				UDP udp = new UDP(raw_data, ipv4.getDataStart());
				this.udp = udp;
			} else if (ipv4.getDataType() == IP.PayloadType.TCP) {
				TCP tcp = new TCP(raw_data, ipv4.getDataStart(), ipv4.getTotalLength()
						- ipv4.getHeaderLength());
				this.tcp = tcp;
			}
		}
 	}

	public Ethernet2 getEthernet2() {
		return ethernet2;
	}
  
	public IP getIp() {
		return ip;
	}
 
 	public TCP getTcp() {
		return tcp;
	}
 
	public UDP getUdp() {
		return udp;
	}

	@Override
	public String toString() {
		return "PackageData [ethernet2=" + ethernet2 + ", ip=" + ip + ", tcp=" + tcp + ", udp=" + udp + "]";
	}
	 
}
