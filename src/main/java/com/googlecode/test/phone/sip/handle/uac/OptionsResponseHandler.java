package com.googlecode.test.phone.sip.handle.uac;

import javax.sip.ResponseEvent;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.AbstractSipPhone;

public class OptionsResponseHandler extends AbstractResponseHandler {
	
	private static final Logger LOG = Logger.getLogger(OptionsResponseHandler.class);

 	public OptionsResponseHandler(AbstractSipPhone sipPhone) {
		super(sipPhone);
 	}

	@Override
	public void handle(ResponseEvent arg0) {
		
		LOG.info(arg0.getResponse());
  		 
	}

}
