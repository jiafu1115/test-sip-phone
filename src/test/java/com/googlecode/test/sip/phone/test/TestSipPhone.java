package com.googlecode.test.sip.phone.test;

import java.io.File;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import com.googlecode.test.phone.AnonymousSipPhone;
import com.googlecode.test.phone.SipPhone;

public class TestSipPhone {

	 	public static void main(String[] args) throws InterruptedException, ParseException {
	 		
	 		
	 		new File("C:/Users/jiafu/git/test-sip-phone");
			
 	
	 	SipPhone sipPhone = new AnonymousSipPhone();
	 	
	 	sipPhone.enablePlayListened();
	 	
	 //	sipPhone.setEarlyOffer(true);
	 	sipPhone.invite("sip:10.224.57.78:5060", "fujian");
	 	TimeUnit.SECONDS.sleep(12);

	 	sipPhone.sendDtmf("12345678#");
	 	
	 	TimeUnit.SECONDS.sleep(12);

 
	 	sipPhone.bye();
	 	
	   // TimeUnit.SECONDS.sleep(4); 
	    
	 	}
	 		
	 /*		//AuthorizationHeader createAuthorizationHeader = SipConstants.Factorys.HEADER_FACTORY.createAuthorizationHeader("Digest");
	 		//createAuthorizationHeader.setAlgorithm("Digest");
	 		//System.out.println(createAuthorizationHeader.toString());
	 		
	 		PhoneExtension phoneExtension=new PhoneExtension("3002", "3002", "10.224.2.213", "sip:10.224.2.213");
	 		RegisteredSipPhone sipPhone = new RegisteredSipPhone(phoneExtension);
	 		sipPhone.test();
	 		
	 		sipPhone.unregister();

			
			sipPhone.register();
			
			TimeUnit.SECONDS.sleep(10);
			
			sipPhone.unregister();
		 
			//sipPhone.invite("3002@10.224.2.213", "fujian");
	 		
		}*/
}
