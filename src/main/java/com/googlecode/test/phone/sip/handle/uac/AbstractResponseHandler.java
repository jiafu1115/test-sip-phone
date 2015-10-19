package com.googlecode.test.phone.sip.handle.uac;

import com.googlecode.test.phone.AbstractSipPhone;

public abstract class AbstractResponseHandler implements ResponseHandler{
	
	protected AbstractSipPhone sipPhone;
 	
	
	public AbstractResponseHandler(AbstractSipPhone sipPhone) {
		super();
		this.sipPhone = sipPhone;
 	}
	
	

}
