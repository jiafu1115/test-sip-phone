package com.googlecode.test.sip.phone.sip;

import javax.sdp.SdpFactory;
import javax.sip.InvalidArgumentException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.message.MessageFactory;

public class SipConstants {
	
	static final String BRANCH_MAGIC_STR = "z9hG4bK";

	private SipConstants(){
		
	}
	
	public static class DefaultHeaders{
 		public static MaxForwardsHeader DEFAULT_MAXFORWARDS_HEADER;

 		static{
 			try {
				DEFAULT_MAXFORWARDS_HEADER=SipConstants.Factorys.HEADER_FACTORY.createMaxForwardsHeader(70);
			} catch (InvalidArgumentException e) {
	 			throw new RuntimeException(e.getMessage(), e);
 			}
 		}

	}
	
	public static class Factorys{
		
		public static SipFactory SIP_FACTORY;
		public static AddressFactory ADDRESS_FACTORY;
		public static HeaderFactory HEADER_FACTORY;
		public static MessageFactory MESSAGE_FACTORY;
		public static SdpFactory SDP_FACTORY;
		
		static{
			
			SIP_FACTORY = SipFactory.getInstance();
			SIP_FACTORY.setPathName("gov.nist");
			try {
				ADDRESS_FACTORY = SIP_FACTORY.createAddressFactory();
				HEADER_FACTORY = SIP_FACTORY.createHeaderFactory();
				MESSAGE_FACTORY = SIP_FACTORY.createMessageFactory();
				SDP_FACTORY = SdpFactory.getInstance();
			} catch (PeerUnavailableException e) {
	 			throw new RuntimeException(e.getMessage(), e);
			}

	 	}
	}
	 

}
