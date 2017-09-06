/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Obsidiam
 */
public class NetController {
    private volatile Socket S;
    
    public NetController(Socket s){
        this.S = s;
    }
    
    public String getIp() throws UnknownHostException, IOException{
         
         return S.getLocalAddress().getHostAddress();
    }

    public boolean isReachable(String ip) throws Exception {
        try {
            if(InetAddress.getByName(ip).isReachable(500)){
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
