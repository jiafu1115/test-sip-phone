package com.googlecode.test.phone.sip.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtil {

    private NetUtil() {
        // no instance;
    }

    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress().toString();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String toIpFromDns(String dnsName) {
        try {
            return InetAddress.getByName(dnsName).getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}