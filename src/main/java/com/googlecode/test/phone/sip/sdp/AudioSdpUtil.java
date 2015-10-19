package com.googlecode.test.phone.sip.sdp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.sdp.Connection;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SdpConstants;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;

import com.googlecode.test.phone.rtp.codec.AudioCodec;
import com.googlecode.test.phone.sip.SipConstants;

import gov.nist.javax.sdp.fields.AttributeField;

public class AudioSdpUtil {

	private static final String AUDIO = "audio";

	public static SessionDescription createSessionDescription(String ip, int audioPort, Set<AudioCodec> audioCodecs)
			throws SdpException, SdpParseException {
		SessionDescription sessionDescription = SipConstants.Factorys.SDP_FACTORY.createSessionDescription();

		Vector<MediaDescription> mediaDescriptions = new Vector<MediaDescription>();
		mediaDescriptions.add(getMediaDescription(audioPort, audioCodecs));

		sessionDescription.setMediaDescriptions(mediaDescriptions);
		sessionDescription.setSessionName(SipConstants.Factorys.SDP_FACTORY.createSessionName("session name"));
		sessionDescription
				.setOrigin(SipConstants.Factorys.SDP_FACTORY.createOrigin("fromUser", SdpFactory.getNtpTime(new Date()),
						SdpFactory.getNtpTime(new Date()), Connection.IN, Connection.IP4, ip));

		Connection sdpConnection = SipConstants.Factorys.SDP_FACTORY.createConnection(Connection.IN, Connection.IP4,
				ip);
		sessionDescription.setConnection(sdpConnection);

		return sessionDescription;
	}

	@SuppressWarnings("unchecked")
	public static AudioSdpMedia parseAudioCodecFromSdpStr(byte[] sdpBytes) {
		String sdpStr = new String(sdpBytes);
 		AudioSdpMedia sdpMedia = new AudioSdpMedia(); 

		try {
			SessionDescription createSessionDescription = SipConstants.Factorys.SDP_FACTORY
					.createSessionDescription(sdpStr);
			Vector<MediaDescription> mediaDescriptions = createSessionDescription.getMediaDescriptions(true);
 			Iterator<MediaDescription> iterator = mediaDescriptions.iterator();
 			
			while (iterator.hasNext()) {
				MediaDescription next = iterator.next();
  				Media media = next.getMedia();
				String mediaType;

				mediaType = media.getMediaType();
				if (mediaType.equalsIgnoreCase(AUDIO)) {
 					Set<AudioCodec> audioCodecs = new HashSet<AudioCodec>();

					Vector<String> mediaFormats = media.getMediaFormats(true);
					for (String payloadTypeStr : mediaFormats) {
						AudioCodec audioCodec = AudioCodec.fromString(Integer.valueOf(payloadTypeStr));
						if(audioCodec!=null)
							audioCodecs.add(audioCodec);
					}
					sdpMedia.setCodec(audioCodecs);
 					sdpMedia.setPort(media.getMediaPort());
 					sdpMedia.setIp(createSessionDescription.getConnection().getAddress());
 				}

			}
		} catch (SdpException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
 		}
		return sdpMedia;

	}

 

	private static MediaDescription getMediaDescription(int audioPort, Set<AudioCodec> audioCodecs)
			throws SdpException {
		int[] audioFmts = getAudioFmts(audioCodecs);
 		MediaDescription mediaDescription = SipConstants.Factorys.SDP_FACTORY.createMediaDescription(AUDIO, audioPort,
				0, SdpConstants.RTP_AVP, audioFmts);

		for (AudioCodec audioCodec : audioCodecs) {
			AttributeField attributeField = new AttributeField();
			attributeField.setValue(audioCodec.toString());
			mediaDescription.addAttribute(attributeField);
 		}
		
		AttributeField attributeField = new AttributeField();
		attributeField.setValue("sendrecv");
		mediaDescription.addAttribute(attributeField);

		return mediaDescription;
	}

	private static int[] getAudioFmts(Set<AudioCodec> audioCodecs) {
		int[] audioFmts = new int[audioCodecs.size()];
 		List<Integer> list = new ArrayList<Integer>();

		for (AudioCodec audioCodec : audioCodecs) {
			list.add(audioCodec.getPayloadType());
		}

		for (int i = 0; i < list.size(); i++) {
			audioFmts[i] = list.get(i);
		}
		return audioFmts;
	}

	public static HashSet<AudioCodec> negotiationCodec(Set<AudioCodec> remoteSupportedAudioCodecs,
			Set<AudioCodec> localSupportAudioCodecs) {
		HashSet<AudioCodec> hashSet = new HashSet<AudioCodec>();;
	
		hashSet.addAll(localSupportAudioCodecs);
		hashSet.retainAll(remoteSupportedAudioCodecs);
		
		HashSet<AudioCodec> finalHashSet = new HashSet<AudioCodec>();
		for(AudioCodec audioCodec: hashSet){
			if(audioCodec!=AudioCodec.TELEPHONE_EVENT){
				finalHashSet.add(audioCodec);
 				break;
			}
		}
		
		if(hashSet.contains(AudioCodec.TELEPHONE_EVENT))
			finalHashSet.add(AudioCodec.TELEPHONE_EVENT);
   		
 		return finalHashSet;
	}

}
