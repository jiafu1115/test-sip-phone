
package com.googlecode.test.sip.phone.media.codec;

import javax.sdp.SdpConstants;

public enum AudioCodec {
   		
		PCMU("PCMU",SdpConstants.PCMU),PCMA("PCMA",SdpConstants.PCMA),TELEPHONE_EVENT("telephone-event",101);
	
		private String name;
		private int payloadType;
		
		private AudioCodec(String name, int payloadType){
			this.name=name;
			this.payloadType=payloadType;
		}

		public String getName() {
			return name;
		}

		public int getPayloadType() {
			return payloadType;
		}
		 
		@Override
		public String toString(){
			return  String.format("rtpmap:%d %s/8000", payloadType,name);
		}
		
		public static AudioCodec fromString(int payloadType){
 			AudioCodec[] values = values();
			for(AudioCodec audioCodec: values){
				if(audioCodec.getPayloadType()==payloadType){
					return audioCodec;
				}
			}
			
			return null;
 		}
	 
	 
}
