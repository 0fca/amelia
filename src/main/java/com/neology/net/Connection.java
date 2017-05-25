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
    
    public Connection(InputStream in, OutputStream out){
        T = new Transport(in,out);
    }
    
    protected void changeState(TransportState state){
        ACTUAL = state;
    }
    
    public void open(){
        ACTUAL.openConnection(T);
    }
    
    public void close(){
        ACTUAL.closeConnection(new Transport());
    }
    
    public void send(TransportState state,byte[] buffer){
        ACTUAL.sendPacket(T, buffer);
    }
    
    public byte[] read(Transport t, byte[] buffer) throws TransportException{
        buffer = ACTUAL.readPacket(t, buffer);
        return buffer;
    }
    
    public boolean isConnected(){
        return T.isConnected();
    }
}
