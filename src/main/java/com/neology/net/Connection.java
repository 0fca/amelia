/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.data.ConnectionDataHandler;
import com.neology.exceptions.TransportException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obsidiam
 */
public class Connection {
    private TransportState ACTUAL;

    private Transport T;
    private Socket s;
    
    public void changeState(TransportState state){
        ACTUAL = state;
    }
    
    public void open(Socket s){
        this.s = s;
        try {
            ACTUAL.openConnection(s.getInputStream(),s.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        T = ACTUAL.getTransportInstance();
    }
    
    public void close(){
        ACTUAL.closeConnection(T, s);
    }
    
    public void halt(){
        ACTUAL.haltConnection(T, s);
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
        return ConnectionDataHandler.getInstance().checkIfIpWasConnected(T.getIp());
    }
    
    public Transport getTranportInstance(){
        return T;
    }

    public void setIp(String ip) {
        T.setIp(ip);
    }
}
