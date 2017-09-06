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
import java.net.Socket;

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
    public void closeConnection(Transport t, Socket s){
        this.closeConnection(t,s);
    }
    @Override
    public void haltConnection(Transport t, Socket s){
        this.haltConnection(t,s);
    }

    @Override
    public void sendPacket(Transport t, byte[] buffer) {
        this.sendPacket(t, buffer);
    }
    
    public byte[] readPacket(Transport t) throws TransportException{
        System.out.println("TransportState readPacket()");
        return this.readPacket(t);
    }
    
    public Transport getTransportInstance(){
        return T;
    }
}
