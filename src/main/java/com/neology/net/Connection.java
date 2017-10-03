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
    
    public Connection(){}
    
    public Connection(Transport t){
        this.T = t;
    }
    
    public void changeState(TransportState state){
        ACTUAL = state;
        
    }
    
    public void open(Socket s){
        this.s = s;
        try {
            ACTUAL.openConnection(s.getInputStream(),s.getOutputStream());
            T = ACTUAL.getTransportInstance();
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public void establish(){
        ACTUAL.establish(T);
    }
    
    public void close(){
        try {
            ACTUAL.closeConnection(T);
            s.close();
        } catch (ClosedConnectionException | IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void halt(){
        ACTUAL.haltConnection(T);
        try {
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(byte[] buffer){
        ACTUAL.sendPacket(buffer);
    }    
    public byte[] read() throws TransportException{
        System.out.println("Connection read()::"+T.getIp());
        return ACTUAL.readPacket();
    }
    
    public void write(byte[] buffer) throws TransportException{
        ACTUAL.write(buffer);
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
