package com.excavator.rpc.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by cmonkey on 3/28/17.
 */
public class NetUtils {
    public static String getLocalIp(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
