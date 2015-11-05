package com.googlecode.test.phone;

import com.google.common.util.concurrent.AbstractFuture;

public class ReferResultFuture extends AbstractFuture<ReferResult> {
	
 	public void setResult(ReferResult referResult){
			this.set(referResult);
	}

}
