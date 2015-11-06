package com.googlecode.test.phone.rtp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.googlecode.test.phone.rtp.dtmf.DtmfRtpPacket;
 
public class RtpSender {

 	private static final int NOISE_PAYLOAD_TYPE = 13;

	private RtpChannel rtpSession;
	private List<RtpPacket> dtmfPackets = Collections
			.synchronizedList(new ArrayList<RtpPacket>());
	private ArrayBlockingQueue<RtpPacket> audioFilePackets = new ArrayBlockingQueue<RtpPacket>(1);
 	
	private int ssrc=new Random().nextInt();
	private int sequenceNumber;
	private int timestamp;
	private int timestampIncreaseSize;


	private ScheduledExecutorService newScheduledThreadPool = Executors
			.newScheduledThreadPool(1);
	
	public RtpSender(RtpChannel rtpSession) {
		this.rtpSession = rtpSession;
		this.timestampIncreaseSize=rtpSession.getPtime()*rtpSession.getSampleRate()/1000;
 	}
	
	public synchronized void start() {
		newScheduledThreadPool.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				RtpPacket newRtpPacket = getNewRtpPacket();
				rtpSession.write(newRtpPacket);
			}
		}, 0, rtpSession.getPtime(),
				TimeUnit.MICROSECONDS);
	}

	public RtpPacket getNewRtpPacket() {
 		RtpPacket rtpPacket = generateBaseRtpPacket();

		if (dtmfPackets.size() > 0) {
			return generateDtmfPackt(rtpPacket);
		}
 
		RtpPacket poll = audioFilePackets.poll();
		if(poll!=null){
			return generateAudioFilePacket(rtpPacket,poll);
 		}
 
		return generateComfortableNoiseRtp(rtpPacket);
 	}
	
	private RtpPacket generateBaseRtpPacket() {
		RtpPacket rtpPacket = new RtpPacket();

		rtpPacket.setVersion(2);
		rtpPacket.setPadding(false);
		rtpPacket.setExtension(false);
		rtpPacket.setCsrcCount(0);
		rtpPacket.setMarker(false);
		rtpPacket.setSsrc(ssrc);
		rtpPacket.setSequenceNumber(sequenceNumber++);
		timestamp += timestampIncreaseSize;
		rtpPacket.setTimestamp(timestamp);

		return rtpPacket;
	}

	private RtpPacket generateComfortableNoiseRtp(RtpPacket rtpPacket) {
		rtpPacket.setPayloadType(NOISE_PAYLOAD_TYPE);
		rtpPacket.setData(new byte[] {});

		return rtpPacket;
	}

	private RtpPacket generateAudioFilePacket(RtpPacket rtpPacket,RtpPacket rtpPacketInAudioFile) {
		//TODO check if payload is same with sdp
 		rtpPacket.setData(rtpPacketInAudioFile.getData());
 		return rtpPacket;
	}

	private RtpPacket generateDtmfPackt(RtpPacket rtpPacket) {
		RtpPacket pushedPacket = dtmfPackets.remove(0);
		rtpPacket.setMarker(pushedPacket.isMarker());
		rtpPacket.setPayloadType(pushedPacket.getPayloadType());
		rtpPacket.setData(pushedPacket.getData());
		// if rtp packet is the first packet in one DTMF event.
		if (pushedPacket instanceof DtmfRtpPacket && pushedPacket.isMarker()) {
			rtpPacket.setTimestamp(timestamp);
			pushedPacket.setTimestamp(timestamp);

		} else {
			DtmfRtpPacket previousDtmfRtpPacket = ((DtmfRtpPacket) pushedPacket)
					.getFirstDtmfRtpPacket();
			long previousTimeStamp = previousDtmfRtpPacket.getTimestamp();
			rtpPacket.setTimestamp(previousTimeStamp);
		}

		return rtpPacket;
	}

  
	public synchronized void stop() {
		newScheduledThreadPool.shutdown();
	}
 
	public void sendDtmfPackets(List<RtpPacket> rtpPackets) {
		this.dtmfPackets.addAll(rtpPackets);
	}

	public void sendAuidoFilePackets(RtpPacket rtpPacket) {
		try {
			this.audioFilePackets.put(rtpPacket);
		} catch (InterruptedException e) {
 			e.printStackTrace();
		}
	}

}
