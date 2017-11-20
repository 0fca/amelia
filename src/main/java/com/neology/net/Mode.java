/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author obsidiam
 */
public final class Mode {
    public static final int REMOTE = 0x100;
    public static final int LOCAL = 0x200;
    private final static Set<String> IPS = new HashSet<>();
    
    static{
        IPS.add("192.168");
        IPS.add("169.254");
        IPS.add("127.0.0");
    }
    public static int getPort(int mode){
        switch(mode){
            case 0x100:
                return 8000;
            case 0x200:
                return 7999;
            default:
                return 7999;
        }
    }
    
    public static boolean isPrivateIp(String ip){
        ip = prepareIp(ip);
        System.out.println(ip);
        return IPS.contains(ip);
    }

    private static String prepareIp(String ip) {
        if(ip.startsWith("127.")){
            String[] split = ip.split(".");
            ip = ip.concat(split[0]+"."+split[1]+"."+split[2]);
        }else{
            ip = ip.substring(0,7);
        }
        return ip;
    }
}
