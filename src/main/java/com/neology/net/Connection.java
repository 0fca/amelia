/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.exceptions.TransportException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author obsidiam
 */
public class Connection {
    private TransportState ACTUAL;

    private Transport T;
    
    
    public void changeState(TransportState state){
        ACTUAL = state;
    }
    
    public void open(InputStream in, OutputStream out){
        ACTUAL.openConnection(in,out);
        T = ACTUAL.getTransportInstance();
    }
    
    public void close(){
        ACTUAL.closeConnection(new Transport());
    }
    
    public void send(TransportState state,byte[] buffer){
        ACTUAL.sendPacket(T, buffer);
    }
    
    public byte[] read(Transport t) throws TransportException{
        System.out.println("Connection read()::"+t.getIp());
        return ACTUAL.readPacket(t);
    }
    
    public boolean isConnected(){
        return T.isConnected();
    }
    
    public boolean wasConnected(){
        return T.wasConnected();
    }
    
    public Transport getTranportInstance(){
        return T;
    }

    public void setIp(String ip) {
        T.setIp(ip);
    }
}
