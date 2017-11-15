/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net.states;

import com.neology.exceptions.ClosedConnectionException;
import com.neology.log.Log;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obsidiam
 */
public class Closed extends TransportState{
    private Transport T;
    
    @Override
    public void closeConnection(Transport t) throws ClosedConnectionException{
        try {
            Log.log("Closed::closeConnection()","attempting to close...");
            this.T = t;
            
            if(t != null){
                    if(t.isTcp()){
                        if(t.isConnected()){
                            t.close(); 
                            Log.log("Closed::closeConnection()","closed.");
                        }else{
                            throw new ClosedConnectionException("Connection already closed!",new Throwable());
                        }
                    }else{
                        if(!t.getDatagramSocket().isClosed()){
                            t.getDatagramSocket().close();
                        }else{
                            throw new ClosedConnectionException("Connection already closed!",new Throwable());
                        }
                    }
            }
        } catch (IOException ex) {
            Logger.getLogger(Closed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void haltConnection(Transport t) {
        try {
            this.T = t;
            if(t.isTcp()){
                t.close();
            }else{
                t.getDatagramSocket().close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Closed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public Transport getTransportInstance(){
        return T;
    }
}
