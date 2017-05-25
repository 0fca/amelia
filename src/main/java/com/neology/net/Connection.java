/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

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
    
    public void open(TransportState state){
        state.openConnection(T);
    }
    
    public void close(TransportState state){
        state.closeConnection(new Transport());
    }
    
    public void send(TransportState state,byte[] buffer){
        state.sendPacket(T, buffer);
    }
    
    public boolean isConnected(){
        return T.isConnected();
    }
}
