/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.net.states.Transport;
import com.neology.net.states.TransportState;
import com.neology.data.ConnectionDataHandler;
import com.neology.exceptions.ClosedConnectionException;
import com.neology.exceptions.TransportException;
import com.neology.net.states.State;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obsidiam
 */
final public class Connection {
    private TransportState ACTUAL;

    private Transport T;
    private Socket s;
    private volatile String name;
    
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
    
    public void establish(Transport t){
        ACTUAL.establish(t);
    }
    
    public void close(){
        try {
            ACTUAL.closeConnection(T, s);
        } catch (ClosedConnectionException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        return ConnectionDataHandler.getInstance().checkIfIpWasConnected(T.getIp().split(":")[0]);
    }
    
    public Transport getTranportInstance(){
        return T;
    }

    public void setIp(String ip) {
        T.setIp(ip);
    }
    
    public State getState(){
        return State.valueOf(ACTUAL.getClass().getSimpleName().toUpperCase());
    }
    
    public synchronized String getConnectionName(){
        return name;
    }
    
    public synchronized void setConnectionName(String name){
        this.name = name;
    }
    
    public boolean isNameSet(){
        return name != null;
    }
}
