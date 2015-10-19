package com.googlecode.test.phone.rtp.listeners;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.rtp.RtpPacket;
import com.googlecode.test.phone.rtp.codec.AudioCodec;
import com.googlecode.test.phone.rtp.codec.Decoder;
import com.googlecode.test.phone.rtp.codec.PcmaDecoder;
import com.googlecode.test.phone.rtp.codec.PcmuDecoder;

public class PlayRtpListener implements RtpListener {

	private final static Logger LOGGER = Logger
			.getLogger(PlayRtpListener.class);

	private AudioFormat audioFormat;
	private SourceDataLine sourceDataLine;
	private DataLine.Info sourceInfo;
	private Decoder decoder;

	public PlayRtpListener(final AudioCodec audioCodec) {
		LOGGER.info("[RTP][media][play listened audio]");

		initDecoder(audioCodec);
		initAudioSystem();
	}

	private void initAudioSystem() {
		audioFormat = new AudioFormat(8000, 16, 1, true, false);
		sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceDataLine.open(audioFormat);
		} catch (LineUnavailableException e) {
			LOGGER.error("source line unavailable", e);
			return;
		}
		sourceDataLine.start();
	}

	private void initDecoder(final AudioCodec audioCodec) {
		switch (audioCodec) {
		case PCMA:
			decoder = new PcmaDecoder();
			break;
		case PCMU:
			decoder = new PcmuDecoder();
			break;
		default:
			throw new RuntimeException("unsupported payload type");
		}
	}

	@Override
	public void receivedRtpPacket(RtpPacket rtpPacket) {
		byte[] rawBuf = decoder.process(rtpPacket.getData());
		sourceDataLine.write(rawBuf, 0, rawBuf.length);
	}

	@Override
	public void close() {
 		sourceDataLine.drain();
		sourceDataLine.stop();
		sourceDataLine.close();
 	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName();
	}
}
