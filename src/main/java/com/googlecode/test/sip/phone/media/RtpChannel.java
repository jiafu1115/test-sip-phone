package com.googlecode.test.sip.phone.media;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

import com.googlecode.test.sip.phone.media.rtp.RtpPacket;
import com.googlecode.test.sip.phone.media.rtp.RtpParserUtil;
import com.googlecode.test.sip.phone.media.rtp.listeners.RtpListener;

public class RtpChannel {
 
	private ConnectionlessBootstrap  bootstrap;
    private Channel channel;
    
    private InetSocketAddress localAddress;
    private InetSocketAddress remoteAddress;
    
 	private List<RtpListener> rtpListeners;
  	private RtpSender rtpSender;
 	private RtpReceiver rtpReceiver;
 	
 	private int ptime=20;
 	private int sampleRate=8000;
   
    public RtpChannel(String localAddress,int localPort, String remoteIp, int remotePort, ArrayList<RtpListener> rtpListeners) {
		super();
		this.localAddress = new InetSocketAddress(localAddress,localPort);
		this.remoteAddress=new InetSocketAddress(remoteIp,remotePort);
		this.rtpListeners=rtpListeners;
  		this.rtpSender = new RtpSender(this);
        this.rtpReceiver = new RtpReceiver(this);
    }
       
	public void start(){
        bootstrap=new ConnectionlessBootstrap(new NioDatagramChannelFactory());
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				 ChannelPipeline pipeline = Channels.pipeline();
			     pipeline.addLast("handler", rtpReceiver);
			     return pipeline;
			}
		});
        bootstrap.setOption("localAddress", localAddress);
        channel=bootstrap.bind();
        
        this.rtpSender.start();
    }
    
    public void write(byte[] content){
        ChannelBuffer responseBuffer= new DynamicChannelBuffer(120);
        
        responseBuffer.writeBytes(content);
        channel.write(responseBuffer, remoteAddress);
    }
     
    public void writeDtmfPackets(List<RtpPacket> rtpPackets) {
		this.rtpSender.sendDtmfPackets(rtpPackets);
	}

	public void writeAuidoFilePackets(List<RtpPacket> rtpPackets) {
		this.rtpSender.sendAuidoFilePackets(rtpPackets);
 	}
 
    public void write(RtpPacket rtpPacket){
    	byte[] encode = RtpParserUtil.encode(rtpPacket);
    	write(encode);
    }
    
    public void addRtpListener(RtpListener rtpListener){
    	this.rtpListeners.add(rtpListener);
    }
   
    
    public void stop(){
    	this.rtpSender.stop();
    	this.channel.close();
    	this.bootstrap.shutdown();
    }
    
    
    public List<RtpListener> getRtpListeners() {
  		return rtpListeners;
  	}

  	public void setRtpListeners(List<RtpListener> rtpListeners) {
  		this.rtpListeners = rtpListeners;
  	}
 
	public int getPtime() {
		return ptime;
	}
 
	public void setPtime(int ptime) {
		this.ptime = ptime;
	}
 
	public int getSampleRate() {
		return sampleRate;
	}
 
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	 
	public void setRtpSender(RtpSender rtpSender) {
		this.rtpSender = rtpSender;
	}

	public void setRtpReceiver(RtpReceiver rtpReceiver) {
		this.rtpReceiver = rtpReceiver;
	}

	@Override
	public String toString() {
		return "RtpSession [localAddress=" + localAddress + ", remoteAddress=" + remoteAddress + ", rtpListeners="
				+ rtpListeners +"]";
	}
	 
    
}