package com.mec.mfct.util;

import java.net.*;
import java.util.Enumeration;

public class NetWorkUtil {

    public static String getLocalHost(){
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface networkInterface = ifaces.nextElement();
                Enumeration<InetAddress> inetAddrs = networkInterface.getInetAddresses();
                while (inetAddrs.hasMoreElements()) {
                    InetAddress addr = inetAddrs.nextElement();
                    if (!addr.isLoopbackAddress()) {
                        if (addr.isSiteLocalAddress()) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }
}
