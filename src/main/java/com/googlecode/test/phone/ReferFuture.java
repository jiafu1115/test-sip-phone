package com.googlecode.test.phone;

import com.google.common.util.concurrent.AbstractFuture;

public class ReferFuture extends AbstractFuture<Integer> {
	
	public void setResult(Integer response){
			this.set(response);
	}

}