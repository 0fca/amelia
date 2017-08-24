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
    private Socket s = null;
    private NetController n = null;
    private HashMap<String,Service> THREADS = null;
    private ConnectionDataHandler CDH = ConnectionDataHandler.getInstance();
    
    {
        s = new Socket();
        n = new NetController(s);
        THREADS = CDH.getThreadsMap();
    }
    
      private Connection initConnection() throws IOException{
            Opened o = new Opened();
            Connection c = new Connection();
            c.changeState(o);
            c.open(s.getInputStream(), s.getOutputStream());
            c.setIp(s.getRemoteSocketAddress().toString());
            return c;
      }

        @Override
        protected Task createTask() {
            return new Task<Void>(){
                @Override
                public Void call(){
                    try {
                        if(ss == null){
                          ss = new ServerSocket(CDH.getPort(),16,new InetSocketAddress(n.getIp(),CDH.getPort()).getAddress());
                          System.out.println("ServerSocket prepared.");
                        }
                        while(!this.isCancelled()){
                            s = ss.accept();
                            if(!s.isClosed() && !ss.isClosed()){
                                IS_CONNECTED = true;
                                System.out.println("Accepted.");
                                    Connection c;
                                    try {
                                        c = initConnection();
                                        BaudrateMeter meter = new BaudrateMeter();
                                        c.getTranportInstance().setBaudrateMeter(meter);
                                        TCPService t = new TCPService(c);
                                        t.start();
                                        THREADS.put(c.getTranportInstance().getIp(), t);
                                       
                                    } catch (IOException ex) {
                                        Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                            }
                        }
                    } catch (IOException ex) {
                          //Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                          s = null;
                          ss = null;
                          System.out.println("ConnectionManager stopped.");
                    }
                return null;
                }
            };
        }
        
        public void closeAllConnections() throws IOException{
            s.close();
            s = null;
            ss.close();
            ss = null;
            this.cancel();
            
        }
        
        public void closeConnection(TCPService t){
            t.cancel();
        }
        
        public boolean isAnyConnected(){
            return IS_CONNECTED;
        }
  }