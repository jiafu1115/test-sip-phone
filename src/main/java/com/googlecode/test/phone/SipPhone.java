package com.googlecode.test.phone;

import com.googlecode.test.phone.sip.ReceivedMessages;

public interface SipPhone {

	void setSupportRefer(boolean isSupportRefer);

	void setEarlyOffer(boolean isEarlyOffer);

	ReceivedMessages getReceivedMessages();

	void enablePlayListened();

	void sendDtmf(String dtmfs);

	void sendDtmf(String digits, int sleepTimeByMilliSecond);

	void invite(String requestUrl, String callId);

	void bye();

	void stop();

}