package com.googlecode.test.sip.phone.media;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.googlecode.test.sip.phone.media.rtp.RtpPacket;
import com.googlecode.test.sip.phone.media.rtp.RtpParserUtil;
import com.googlecode.test.sip.phone.media.rtp.listeners.RtpListener;

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
        super.exceptionCaught(ctx, e);
    }
    
}