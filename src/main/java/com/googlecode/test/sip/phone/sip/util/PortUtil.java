package com.googlecode.test.sip.phone.sip.util;

import java.io.IOException;
import java.net.DatagramSocket;

public class PortUtil {
	
	
 
	/**
	 * allocate one free port
	 * 
	 * @return free port
	 */
	public synchronized static int allocateLocalPort() {

		try {
			DatagramSocket datagramSocket = new DatagramSocket();
			int localPort = datagramSocket.getLocalPort();
			datagramSocket.close();
			
 			return localPort;
		} catch (IOException e) {
			throw new RuntimeException("allocateLocalPort()", e);
		}
	}
	 
	 
}
