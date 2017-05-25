/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.exceptions.TransportException;
import com.neology.interfaces.Connectable;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author obsidiam
 */
public class TransportState implements Connectable{
    private Transport T;
    @Override
    public  void openConnection(InputStream in, OutputStream out){
        Opened o = new Opened();
        o.openConnection(in,out);
        T = o.getTransportInstance();
    }
    @Override
    public void closeConnection(Transport t){
        new Closed().closeConnection(t);
    }
    @Override
    public void haltConnection(Transport t){
        new Closed().haltConnection(t);
    }
    @Override
    public boolean wasConnected(Transport t){
        return t.wasConnected();
    }
    
    @Override
    public boolean isConnected(Transport t){
        return t.isConnected();
    }

    @Override
    public void sendPacket(Transport t, byte[] buffer) {
        new Established().sendPacket(t, buffer);
    }
    
    public byte[] readPacket(Transport t, byte[] buffer) throws TransportException{
        return new Established().readPacket(t, buffer);
    }
    
    public Transport getTransportInstance(){
        return T;
    }
}