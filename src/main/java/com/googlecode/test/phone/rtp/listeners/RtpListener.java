 
package com.googlecode.test.phone.rtp.listeners;

import com.googlecode.test.phone.rtp.RtpPacket;

public interface RtpListener {

    public void receivedRtpPacket(RtpPacket rtpPacket);
    
    public void close();

}
