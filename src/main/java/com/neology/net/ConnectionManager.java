/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.data.ConnectionDataHandler;
import com.neology.data.ImageDataHandler;
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
            cdh.getConnectionList().stream().map((con) -> {
                return con;
            }).filter((con) -> (con.getState() == com.neology.net.states.State.ESTABLISHED)).forEachOrdered((con) -> {
                con.establish(con.getTranportInstance());
            });
        
            cdh.getConnectionList().stream().map((con) -> {
                return con;
            }).filter((con) -> (con.getState() == com.neology.net.states.State.CLOSED)).forEachOrdered((con) -> {
                con.close();
                cdh.getConnectionList().remove(con);
                idh.getImagesMap().remove(con.getConnectionName());
                
            });
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(wasInterrupted){
                mgr.interrupt();
            }
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

