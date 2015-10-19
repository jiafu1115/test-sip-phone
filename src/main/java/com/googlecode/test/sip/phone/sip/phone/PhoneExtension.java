package com.googlecode.test.sip.phone.sip.phone;

/**
 * @author jiafu
 *3001,3001,10.224.2.213,sip:10.224.2.213;
 */
public class PhoneExtension {

	private String user;
	private String password;
	private String domain;
	private String outboundProxy;
 
	public PhoneExtension(String user, String password, String domain, String outboundProxy) {
		this.user = user;
		this.password = password;
		this.domain = domain;
		this.outboundProxy = outboundProxy;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getDomain() {
		return domain;
	}

	public String getOutboundProxy() {
		return outboundProxy;
	}


	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setOutboundProxy(String outboundProxy) {
		this.outboundProxy = outboundProxy;
	}

	@Override
	public String toString() {
		return "Extension [user=" + user + ", password=" + password + ", domain=" + domain + ", outboundProxy="
				+ outboundProxy + "]";
	}

}
