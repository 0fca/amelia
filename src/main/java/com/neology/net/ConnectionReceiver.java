/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.net.states.Opened;
import com.neology.data.ConnectionDataHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author obsidiam
 */
public class ConnectionReceiver extends Service {
    private boolean IS_CONNECTED = false;
    private ServerSocket ss = null;
    private final ConnectionDataHandler CDH = ConnectionDataHandler.getInstance();
    final List<Connection> list;
    
    {
        list = CDH.getConnectionList();
    }
    
    private Connection initConnection(Socket s) throws IOException{
            Opened o = new Opened();
            Connection c = new Connection();
            c.changeState(o);
            c.open(s);
            c.setIp(s.getRemoteSocketAddress().toString());
            
            return c;
    }

        @Override
        protected Task createTask() {
            return new Task<Void>(){

                @Override
                public Void call() throws IOException, InterruptedException{
                    try {

                            this.updateTitle("ConnectionManager");
                            if(ss == null){
                              ss = new ServerSocket(CDH.getPort(),17);

                              System.out.println("ServerSocket prepared.");
                            }

                            while(!this.isCancelled()){
                                    if(ss != null){
                                        Socket s = ss.accept();

                                        if(!ss.isClosed()){
                                            System.out.println("Server Accepted Connection Request from "+s.getInetAddress().toString());
                                                Connection c;
                                                try {
                                                    c = initConnection(s);
                                                    BaudrateMeter meter = new BaudrateMeter();
                                                    c.getTranportInstance().setBaudrateMeter(meter);

                                                    list.add(c);
                                                    
                                                } catch (IOException ex) {
                                                    Logger.getLogger(ConnectionReceiver.class.getName()).log(Level.SEVERE, null, ex);
                                                    break;
                                                }
                                        }
                                    }
                            }
                        
                    } catch (IOException ex) {
                           System.out.println("ConnectionReceiver stopped.");
                    }
                return null;
                }
            };
        }
        
        private void closeServerSocket() throws IOException{
            if(ss != null){
                ss.close();
                ss = null;
            }
        }
        
        @Override
        public boolean cancel(){
            try {
                closeServerSocket();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
            return super.cancel();
        }
        
        private boolean isAnyConnected(){
            return IS_CONNECTED;
        }
  }