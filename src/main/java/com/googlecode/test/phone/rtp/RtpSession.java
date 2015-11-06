 
package com.googlecode.test.phone.rtp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.rtp.codec.AudioCodec;
import com.googlecode.test.phone.rtp.dtmf.DtmfFactory;
import com.googlecode.test.phone.rtp.listeners.CollectDtmfRtpListener;
import com.googlecode.test.phone.rtp.listeners.PlayRtpListener;
import com.googlecode.test.phone.rtp.listeners.RtpListener;
import com.googlecode.test.phone.rtp.pcap.PCAPPackage;
import com.googlecode.test.phone.rtp.pcap.PCAPParser;

public class RtpSession {

 	private CollectDtmfRtpListener collectDtmfRtpListener=new CollectDtmfRtpListener();
	private RtpChannel rtpChannel;
	private DtmfFactory dtmfFactory;
 	private AudioCodec audioCodec;
	 
	private final static Logger LOGGER = Logger.getLogger(RtpSession.class);

	public RtpSession(String localAddress, int localPort, String remoteAddress, int remotePort, Set<AudioCodec> audioCodecs) {
		this.dtmfFactory = new DtmfFactory();
		LOGGER.info("[RTP][Same Codecs]"+audioCodecs);
		audioCodecs.remove(AudioCodec.TELEPHONE_EVENT);
		audioCodec=audioCodecs.iterator().next();
		LOGGER.info("[RTP][Audio Codec Choose]"+audioCodec);
		initRtpChannel(localAddress, localPort, remoteAddress, remotePort, audioCodecs);
	}

	private void initRtpChannel(String localAddress, int localPort, String remoteAddress, int remotePort,
			Set<AudioCodec> audioCodecs) {
		try {
   			ArrayList<RtpListener> rtpListeners = new ArrayList<RtpListener>();
 			rtpListeners.add(collectDtmfRtpListener);
			rtpChannel = new RtpChannel(localAddress, localPort,
					remoteAddress, remotePort,rtpListeners);
 		} catch (Exception e1) {
			throw new RuntimeException(e1.getMessage(),e1);
 		}

	}
	
	public void start() {
  		try {
  			rtpChannel.start();
  		} catch (Exception e1) {
			throw new RuntimeException(e1.getMessage(),e1);
 		}

		LOGGER.info("[RTP][CREATE]" + rtpChannel);
	}
	
	public void enablePlay(){
		PlayRtpListener playRtpListener = new PlayRtpListener(this.audioCodec);
		rtpChannel.addRtpListener(playRtpListener);
 	}
	
	 
	public void stop() {
 		if (rtpChannel != null) {
 	 		LOGGER.info("[RTP][CLOSE]"+rtpChannel);
 			rtpChannel.stop();
 			rtpChannel = null;
		}
 	} 
	
	public List<Character> getReceivedDtmfs(){
		return this.collectDtmfRtpListener.getDtmfChars();
	}
	
	public void sendDtmf(String digits) {
		 sendDtmf(digits,0);
	}
	
	public void playPcapfile(String fileName){
	 	LOGGER.info("[RTP][PLAY][filename]"+fileName);
   		List<RtpPacket> rtpPackets=new ArrayList<RtpPacket>();
 		PCAPParser pcapParser = new PCAPParser(fileName);
		try{
 	  		PCAPPackage nextRtpPackage=null;
			while((nextRtpPackage = pcapParser.getNextRtpPackage())!=null){
				RtpPacket rtpPacket = RtpParserUtil.decode(nextRtpPackage.getPackageData().getUdp().getUDPData());
				rtpPackets.add(rtpPacket);
			}
		}finally{
			pcapParser.close();
		}
		
	 	LOGGER.info("[RTP][PLAY][packet number]"+rtpPackets.size());
  		this.rtpChannel.writeAuidoFilePackets(rtpPackets);
	}
 
	public void sendDtmf(String digits, int sleepTimeByMilliSecond) {
		LOGGER.info("[RTP][media][send][DTMF] :" + digits);
  		char[] digitArray = digits.toCharArray();
		for (char digit : digitArray) {
			List<RtpPacket> rtpPackets = dtmfFactory.createDtmfPackets(digit);
			rtpChannel.writeDtmfPackets(rtpPackets);
			
			if(sleepTimeByMilliSecond>0){
				try {
					TimeUnit.MILLISECONDS.sleep(sleepTimeByMilliSecond);
				} catch (InterruptedException e) {
 				}
			}
		}
	}
	 
}
