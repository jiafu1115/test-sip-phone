package com.googlecode.test.phone.sip.handle.uas;

import com.googlecode.test.phone.AbstractSipPhone;

public abstract class AbstractRequestHandler implements RequestHandler{
	
	protected AbstractSipPhone sipPhone;
 	
	
	public AbstractRequestHandler(AbstractSipPhone sipPhone) {
		super();
		this.sipPhone = sipPhone;
 	}
	
	

}
