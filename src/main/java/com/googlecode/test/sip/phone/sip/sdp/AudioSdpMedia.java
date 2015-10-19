package com.googlecode.test.sip.phone.sip.sdp;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.test.sip.phone.media.codec.AudioCodec;

public class AudioSdpMedia {
	
	private String ip;
	private int port;
	private Set<AudioCodec> codec=new HashSet<AudioCodec>();	
	
	public AudioSdpMedia(String ip, int port, Set<AudioCodec> codec) {
		super();
		this.ip = ip;
		this.port = port;
		this.codec = codec;
	}
 	
	public AudioSdpMedia() {
 
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
		return "AudioSdpMedia [ip=" + ip + ", port=" + port + ", codec=" + codec + "]";
	}
	
}
