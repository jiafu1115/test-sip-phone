package com.googlecode.test.sip.phone.test;

import java.io.File;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.googlecode.test.phone.AnonymousSipPhone;
import com.googlecode.test.phone.ReferResultFuture;
import com.googlecode.test.phone.SipPhone;

public class TestSipPhone {

	 	public static void main(String[] args) throws InterruptedException, ParseException, ExecutionException {
	 		 
	 		/*	 SipPhone sipPhone = new AnonymousSipPhone();
		 	System.out.println(sipPhone.getLocalSipUri());  
 */
  
   
	   SipPhone sipPhone = new AnonymousSipPhone();

 	 	
 	 	sipPhone.setSupportRefer(true);
 	 	
 	 	sipPhone.enablePlayListened();
	 	
 	 	sipPhone.invite("sip:5510571803@10.224.89.189:5060", "fujian22222");
	 	TimeUnit.SECONDS.sleep(8);

	 	sipPhone.sendDtmf("12345678#");
	 	
	 	TimeUnit.SECONDS.sleep(5);

	 	sipPhone.sendDtmf("1#");

	 	
	 	TimeUnit.SECONDS.sleep(300); 

 
	 	//sipPhone.bye();
	 	 
 	  
	 	}
	 	
	 	
}
