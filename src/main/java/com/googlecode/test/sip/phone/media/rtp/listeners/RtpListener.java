 
package com.googlecode.test.sip.phone.media.rtp.listeners;

import com.googlecode.test.sip.phone.media.rtp.RtpPacket;

public interface RtpListener {

    public void receivedRtpPacket(RtpPacket rtpPacket);
    
    public void close();

}
