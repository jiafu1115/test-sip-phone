package com.googlecode.test.sip.phone.test;

import javax.sip.ListeningPoint;
import javax.sip.address.Address;
import javax.sip.address.SipURI;

import com.googlecode.test.phone.sip.SipConstants;

public class TestSipStack {
	
 
	public static void main(String[] args) throws Exception {
		
		
		 SipURI sipUrl =   SipConstants.Factorys.ADDRESS_FACTORY.createSipURI(null, "10.224.54.90");
        sipUrl.setPort(5060);
        sipUrl.setTransportParam(ListeningPoint.UDP);
        System.out.println(sipUrl);
        Address createAddress = SipConstants.Factorys.ADDRESS_FACTORY.createAddress(sipUrl);
		System.out.println(createAddress);
        System.out.println(createAddress.getURI());
        Address createAddress2 = SipConstants.Factorys.ADDRESS_FACTORY.createAddress("sip:10.224.54.90:5060;transport=udp");
        System.out.println(createAddress2.getURI());


        
    /*    sip:10.224.54.90:5060;transport=udp
        <sip:10.224.54.90:5060;transport=udp>*/
		
	}	

}
