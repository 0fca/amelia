/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.net.states.Closed;
import com.neology.data.ConnectionDataHandler;
import com.neology.data.ImageDataHandler;
import com.neology.net.states.Established;
import com.neology.net.states.Opened;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obsidiam
 */
public class ConnectionManager extends Thread implements Runnable{
    private Thread mgr;
    private ConnectionDataHandler cdh = ConnectionDataHandler.getInstance();
    private ImageDataHandler idh = ImageDataHandler.getInstance();
    private boolean wasInterrupted;
    
    @Override
    public void start(){
        if(mgr == null){
            mgr = new Thread(this, "ConnectionManager");
            mgr.start();
        }
    }
    
    @Override
    public void run(){
        while(!mgr.isInterrupted()){
            for(Connection con : cdh.getConnectionList()){
                if(con.getState() == com.neology.net.states.State.CLOSED){
                    con.close();
                    cdh.getConnectionList().remove(con);
                    idh.getImagesMap().remove(con.getConnectionName());
                }
            
                if(con.getState() == com.neology.net.states.State.ESTABLISHED){
                    con.establish(con.getTranportInstance());
                }
                }
            }
        
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(wasInterrupted){
                mgr.interrupt();
            }
        }
    
    public void interruptThread(){
        if(mgr.getState() != State.TIMED_WAITING){
            mgr.interrupt();
        }else{
            wasInterrupted = true;
        }
    }
    }

