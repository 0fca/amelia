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
import com.neology.net.states.Established;
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
    private boolean wasInterrupted = false;
    private volatile AccessorImpl acc;
    private ConnectionListChangeListenerImpl clcli = new ConnectionListChangeListenerImpl();
    
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
        
        cons.addListener(clcli);
        
        while(mgr != null){
            if(!mgr.isInterrupted() && !wasInterrupted){
                try {
                    synchronized(cons){
                        while(cdh.isFree()){
                            System.out.println("ConnectionManager#run() -> Waiting on cdh.");
                            cons.wait();
                        }

                        if(cons.size() > 0){
                            for(int i = 0; i < cons.size(); i++){
                                Connection con = cons.get(i);
                                if(con != null){
                                    if( con.getState() == com.neology.net.states.State.CLOSED){
                                        con.close();
                                        cons.remove(i);
                                        System.out.println(i);
                                    }else{
                                        if( con.getState() == com.neology.net.states.State.OPENED){
                                            Established e = new Established();
                                            con.changeState(e);
                                            con.establish();
                                            cdh.addConnectionName(con.getTranportInstance().getIp().split(":")[0]);
                                        }
                                    }
                                }
                            }
                            
                        }
                        cdh.setFree(true);
                        cons.notifyAll();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                }

                if(wasInterrupted){
                    interruptThread();
                    break;
                }
            }
        }
        cons.removeListener(clcli);
        cons.clear();
        wasInterrupted = false;
    }
    
    public void interruptThread(){
        disconnectAll();
    }

    private void disconnectAll() {
        
        if(mgr.getState() != State.WAITING){
            mgr.interrupt();
            mgr = null;
        }else{
            wasInterrupted = true;
        }
        
        cdh.getConnectionList().forEach(con ->{
            cdh.addIpToMap(con.getTranportInstance().getIp().split(":")[0]);
            if(con.getState() != com.neology.net.states.State.CLOSED){
                Closed c = new Closed();
                con.changeState(c);
            }
        });  
        
        cdh.setFree(true);
    }
    
    public void setAccessorInstance(AccessorImpl a){
        this.acc = a;
    }

    private class ConnectionListChangeListenerImpl implements ConnectionListChangeListener {

        public ConnectionListChangeListenerImpl() {
        }
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
            if(mgr != null){
                if(acc.checkViewUpdaterAccess(mgr)){
                    acc.sendNotificationSignal(SignalType.WARNING);
                    acc.commitSignalData("Connection with "+local.getConnectionName()+" is "+local.getState().toString().toLowerCase());
                }
            }else{
                acc.sendNotificationSignal(SignalType.WARNING);
                acc.commitSignalData("Connection with "+local.getConnectionName()+" is "+local.getState().toString().toLowerCase());
            }
        }
    }
}


