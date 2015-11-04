package com.googlecode.test.phone.rtp;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.googlecode.test.phone.rtp.listeners.RtpListener;

public class RtpReceiver extends SimpleChannelUpstreamHandler{
	
  	private RtpChannel rtpSession;
  	
    public RtpReceiver(RtpChannel rtpSession) {
		this.rtpSession=rtpSession;
 	}

	@Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        ChannelBuffer buffer = (ChannelBuffer)e.getMessage();
        byte[] recByte=buffer.copy().toByteBuffer().array();
           
  		RtpPacket rtpPacket = RtpParserUtil.decode(recByte);
 		List<RtpListener> rtpListeners = rtpSession.getRtpListeners();
  
		for (RtpListener rtpListener : rtpListeners) {
			rtpListener.receivedRtpPacket(rtpPacket);
		}
        
        super.messageReceived(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        //ignore it
    }
    
}