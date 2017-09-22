/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.controllers.AccessorImpl;
import com.neology.controllers.SignalType;
import com.neology.data.ConnectionDataHandler;
import com.neology.data.ImageDataHandler;
import com.neology.listeners.ConnectionListChangeListener;
import com.neology.net.states.Closed;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author obsidiam
 */
public class ConnectionManager extends Thread implements Runnable{
    private volatile Thread mgr;
    private final ConnectionDataHandler cdh = ConnectionDataHandler.getInstance();
    private final ImageDataHandler idh = ImageDataHandler.getInstance();
    private boolean wasInterrupted;
    private volatile AccessorImpl acc;
    
    @Override
    public void start(){
        if(mgr == null){
            mgr = new Thread(this, "ConnectionManager");
            mgr.start();
        }
    }
    
    @Override
    public void run(){
        ObservableList<Connection> cons = cdh.getConnectionList();
        cons.addListener(new ConnectionListChangeListener() {
                private Connection local;

                @Override
                public void onChanged(ListChangeListener.Change c) {
                    if(c.next()){
                       if(c.wasAdded()){
                           int last = c.getAddedSize();
                           local = (Connection)c.getAddedSubList().get(last - 1);
                       }
                       
                       if(c.wasRemoved()){
                           int last = c.getRemovedSize();
                           local = (Connection)c.getRemoved().get(last - 1);
                           idh.getImagesMap().remove(local.getConnectionName());
                           sendNotificationSignal();
                           
                       }
                    }
                }

                @Override
                public void sendNotificationSignal() {
                    if(acc.checkViewUpdaterAccess(mgr)){
                        acc.sendNotificationSignal(SignalType.WARNING);
                        acc.commitSignalData("Connection with "+local.getConnectionName()+" is "+local.getState().toString().toLowerCase());
                    }
                }
            });
        
        while(!mgr.isInterrupted()){
            try {
                synchronized(cons){
                    while(cdh.isFree()){
                        System.out.println("ConnectionManager#run() -> Waiting on cdh.");
                        cons.wait();
                    }
                    Set<Connection> toBeRemoved = new HashSet<>();
                    
                    if(cons.size() > 0){
                        for(Connection con : cons){
                            if(con != null){
                                if( con.getState() == com.neology.net.states.State.CLOSED){
                                    con.close();
                                    toBeRemoved.add(con);
                                }else{

                                    if( con.getState() == com.neology.net.states.State.ESTABLISHED){
                                        con.establish(con.getTranportInstance());
                                    }
                                }
                            }
                        }
                        cons.removeAll(toBeRemoved);
                        toBeRemoved.clear();
                        cdh.setFree(true);
                        cons.notifyAll();
                    }
                }
                //Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(wasInterrupted){
                mgr.interrupt();
            }
        }
    }
    
    public void interruptThread(){
        if(mgr.getState() != State.WAITING){
            mgr.interrupt();
        }else{
            wasInterrupted = true;
        }
        
        disconnectAll();
    }

    private void disconnectAll() {
        cdh.getConnectionList().forEach(con ->{
            cdh.addIpToMap(con.getTranportInstance().getIp().split(":")[0]);
            if(con.getState() != com.neology.net.states.State.CLOSED){
                Closed c = new Closed();
                con.changeState(c);
                con.close();
            }
        });
        cdh.getConnectionList().clear();
    }
    
    public void setAccessorInstance(AccessorImpl a){
        this.acc = a;
    }
}


