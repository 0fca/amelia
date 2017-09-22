/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net.states;

import com.neology.exceptions.ClosedConnectionException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obsidiam
 */
public class Closed extends TransportState{
    private Transport T;
    
    @Override
    public void closeConnection(Transport t, Socket s) throws ClosedConnectionException{
        try {
            System.out.println("Closed::closeConnection() -> attempting to close...");
            this.T = t;
            if(t != null){
                if(t.isConnected() && !s.isClosed()){
                    t.close();
                    s.close();
                    T = null;
                    System.out.println("Closed::closeConnection() -> closed.");
                }else{
                    throw new ClosedConnectionException("Connection already closed!",new Throwable());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Closed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void haltConnection(Transport t, Socket s) {
        try {
            this.T = t;
            t.close();
            s.close();
            T = null;
        } catch (IOException ex) {
            Logger.getLogger(Closed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public Transport getTransportInstance(){
        return T;
    }
}
