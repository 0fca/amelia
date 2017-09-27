/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net.states;

import com.neology.exceptions.ClosedConnectionException;
import com.neology.exceptions.TransportException;
import com.neology.interfaces.Connectable;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author obsidiam
 */
public class TransportState implements Connectable{
    protected Transport T;
    
    @Override
    public  void openConnection(InputStream in, OutputStream out){
        Opened o = new Opened();
        o.openConnection(in,out);
        T = o.getTransportInstance();
    }
    @Override
    public void closeConnection(Transport t) throws ClosedConnectionException{
        this.closeConnection(t);
    }
    @Override
    public void haltConnection(Transport t){
        this.haltConnection(t);
    }

    @Override
    public void sendPacket(byte[] buffer) {
        this.sendPacket(buffer);
    }
    
    @Override
    public byte[] readPacket() throws TransportException{
        System.out.println("TransportState readPacket()");
        return this.readPacket();
    }
    
    @Override
    public byte[] readPacket( int bufferLen) throws TransportException{
       System.out.println("TransportState readPacket(t,bufferlen)");
       return this.readPacket(bufferLen);
    }
    
    @Override
    public void establish(Transport t){
        this.establish(t);
    }
    
    public Transport getTransportInstance(){
        return T;
    }
    
    @Override
    public String toString(){
        return this.getClass().getCanonicalName();
    }
}
