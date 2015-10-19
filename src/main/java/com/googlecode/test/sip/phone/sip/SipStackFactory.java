package com.googlecode.test.sip.phone.sip;

import java.util.Properties;

import javax.sip.PeerUnavailableException;
import javax.sip.SipStack;

import org.apache.commons.lang3.RandomStringUtils;

public class SipStackFactory {
	
	private static SipStackFactory INSTANCE=new SipStackFactory();
	
	public static SipStackFactory getInstance(){
		return INSTANCE;
	}
	
	
	public SipStack createSipStack(){
  		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "Test_Sip_Phone"+"_"+RandomStringUtils.randomAlphabetic(10));
 		//properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "DEBUG");

		//properties.setProperty("gov.nist.javax.sip.STACK_LOGGER", "gov.nist.core.LogWriter");
		
		//CommonLogger   CommonLoggerLog4j
		
		
		try {
			SipStack createSipStack = SipConstants.Factorys.SIP_FACTORY.createSipStack(properties);
 			return createSipStack;
		} catch (PeerUnavailableException e) {
			throw new RuntimeException(e.getMessage(), e);
 		}
 		
	}
  
}
