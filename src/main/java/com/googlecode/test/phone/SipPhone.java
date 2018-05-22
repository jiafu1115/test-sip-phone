package com.googlecode.test.phone;

import javax.sip.address.SipURI;

import com.googlecode.test.phone.AbstractSipPhone.ReceivedCallHandleType;
import com.googlecode.test.phone.AbstractSipPhone.RequestRender;
import com.googlecode.test.phone.sip.ReceivedMessages;

public interface SipPhone {

	void setSupportRefer(boolean isSupportRefer);

	void setEarlyOffer(boolean isEarlyOffer);

	ReceivedMessages getReceivedMessages();

	void enablePlayListened();

	void sendDtmf(String dtmfs);

	void sendDtmf(String digits, int sleepTimeByMilliSecond);

	void invite(String requestUrl, String callId);
	
	void invite(String requestUrl,String callId, RequestRender requestRender);

	void bye();

	void stop();
	
 	ReceivedCallHandleType getReceivedCallHandleType();

	void setReceivedCallHandleType(ReceivedCallHandleType receivedCallHandleType);
	
	SipURI getLocalSipUri();

	void sendAudioPcapfile(String pcapfileName);

}