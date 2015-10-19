package com.googlecode.test.phone.sip.sdp;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.test.phone.rtp.codec.AudioCodec;

public class SdpInfo {
	
	private String ip;
	private int port;
	private Set<AudioCodec> codec=new HashSet<AudioCodec>();	
	
	public SdpInfo(String ip, int port, Set<AudioCodec> codec) {
		super();
		this.ip = ip;
		this.port = port;
		this.codec = codec;
	}
 	
	public SdpInfo() {
 
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public Set<AudioCodec> getCodec() {
		return codec;
	}
	
	public void setCodec(Set<AudioCodec> codec) {
		this.codec = codec;
	}
	 
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		return "SdpInfo [ip=" + ip + ", port=" + port + ", codec=" + codec + "]";
	}
	
}
