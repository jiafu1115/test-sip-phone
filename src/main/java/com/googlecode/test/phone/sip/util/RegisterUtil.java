package com.googlecode.test.phone.sip.util;

 

 

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.sip.header.AuthorizationHeader;
import javax.sip.header.WWWAuthenticateHeader;

import org.apache.log4j.Logger;

import com.googlecode.test.phone.PhoneExtension;
import com.googlecode.test.phone.sip.SipConstants;

import gov.nist.javax.sip.header.ParameterNames;
import gov.nist.javax.sip.message.SIPRequest;

 

public class RegisterUtil  {

	public static final String ALGORITHM_MD5 = "MD5";

	private final static Logger LOGGER = Logger.getLogger(RegisterUtil.class);
  
	/**
	 * 
 //WWW-Authenticate: Digest algorithm=MD5, realm="asterisk", nonce="48e04948"
	//Authorization: Digest username="3001", realm="asterisk", nonce="48e04948", uri="sip:10.224.2.213", response="3edef90dc5ae5b59d299948e04439a40"
	 * 				 Digest username="3001", realm="asterisk", nonce="48e04948", uri="sip:10.224.2.213", response="3edef90dc5ae5b59d299948e04439a40"
*/
 
	public static AuthorizationHeader createResponseAuthHeaderStr(PhoneExtension phoneExtension,WWWAuthenticateHeader wwwAuthenticateHeader) {
		String username=phoneExtension.getUser();
		String password=phoneExtension.getPassword();
		String requestUri = phoneExtension.getOutboundProxy();
  		String realm = wwwAuthenticateHeader.getRealm();
 		String nonce = wwwAuthenticateHeader.getNonce();
		String opaque =wwwAuthenticateHeader.getOpaque();
 		String digest = getDigest(username, password, requestUri, realm, nonce);
 		
 		
 		AuthorizationHeader createAuthorizationHeader=null;
 
 		try {
 			createAuthorizationHeader = SipConstants.Factorys.HEADER_FACTORY.createAuthorizationHeader(ParameterNames.DIGEST);
 			createAuthorizationHeader.setNonce(nonce);
 	 		createAuthorizationHeader.setResponse(digest);
			createAuthorizationHeader.setRealm(realm);
	 		createAuthorizationHeader.setUsername(username);
	 		if(opaque!=null)
	 			createAuthorizationHeader.setOpaque(opaque);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
 		}
  		
  	 
    	return createAuthorizationHeader;
 	}
 
	private static String getDigest(String username, String password, String requestUri, String realm, String nonce) {
		StringBuffer buf1 = new StringBuffer();
		buf1.append(username);
		buf1.append(":");
		buf1.append(realm);
		buf1.append(":");
		buf1.append(password);
		String ha1 = md5hash(buf1.toString());
		buf1 = new StringBuffer();
		buf1.append(SIPRequest.REGISTER);
		buf1.append(":");
		buf1.append(requestUri);
		String ha2 = md5hash(buf1.toString());
		buf1 = new StringBuffer();
		buf1.append(ha1);
		buf1.append(":");
		buf1.append(nonce);
		buf1.append(":");
		buf1.append(ha2);
		
 		return md5hash(buf1.toString());
	}
 
	private static String md5hash(String message) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance(ALGORITHM_MD5);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("no such algorithm " + ALGORITHM_MD5, e);
			return null;
		}
		byte[] messageBytes = message.getBytes();
		byte[] messageMd5 = messageDigest.digest(messageBytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(out);
		for (byte b : messageMd5) {
			int u_b = (b < 0) ? 256 + b : b;
			printStream.printf("%02x", u_b);
		}
		return out.toString();
	}
  
	 
	 
}


 