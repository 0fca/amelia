/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.data.ConnectionDataHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author obsidiam
 */
public class ConnectionManager extends Service {
    private boolean IS_CONNECTED = false;
    private ServerSocket ss = null;
    private HashMap<String,Service> THREADS = null;
    private ConnectionDataHandler CDH = ConnectionDataHandler.getInstance();
    
    {
        THREADS = CDH.getThreadsMap();
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
                public Void call() throws IOException{
                    try {
                        if(ss == null){
                          ss = new ServerSocket(CDH.getPort());
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
                                            TCPService t = new TCPService(c);

                                            THREADS.put(c.getTranportInstance().getIp(), t);
                                            t.start();
                                        } catch (IOException ex) {
                                            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                                            break;
                                        }
                                }
                            }
                        }
                    } catch (IOException ex) {
                           //Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                           System.out.println("ConnectionManager stopped.");
                    }
                return null;
                }
            };
        }
        
        private void closeAllConnections() throws IOException{
            ss.close();
            ss = null;
            CDH.getThreadsMap().values().stream().forEachOrdered( t ->{
                t.cancel();
            });
            CDH.getThreadsMap().clear(); 
        }
        
        @Override
        public boolean cancel(){
            try {
                closeAllConnections();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return super.cancel();
        }
        
        public void closeConnection(TCPService t){
            t.cancel();
        }
        
        public boolean isAnyConnected(){
            return IS_CONNECTED;
        }
  }