package com.googlecode.test.phone.sip.jain;

import javax.sip.header.Header;
import javax.sip.header.HeaderAddress;
import javax.sip.header.Parameters;

public interface AlsoHeader extends HeaderAddress, Parameters, Header {
	String NAME = "Also";
}