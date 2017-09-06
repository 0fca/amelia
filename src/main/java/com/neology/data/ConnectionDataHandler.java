/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.data;

import java.util.HashMap;
import java.util.HashSet;
import javafx.concurrent.Service;

/**
 *
 * @author obsidiam
 */
public class ConnectionDataHandler {
     private volatile HashMap<String,Service> THREADS = new HashMap<>();   
     private volatile HashMap<String,String> DATA = new HashMap<>();
     private volatile HashSet<String> IPS_MAP = new HashSet<>(); 
     private static volatile ConnectionDataHandler INSTANCE = new ConnectionDataHandler();
     
     private ConnectionDataHandler(){}
     
     public synchronized static ConnectionDataHandler getInstance(){
         return INSTANCE;
     }
     
     private volatile int PORT = 7999;
     
     public synchronized HashMap<String,Service> getThreadsMap(){
         return THREADS;
     }
     
     public synchronized void setPort(int port){
         this.PORT = port;
     }
     
     public synchronized int getPort(){
         return PORT;
     }
     
     public synchronized HashMap<String,String> getData(){
         return DATA;
     }
     
     public synchronized boolean checkIfIpWasConnected(String ip){
         return IPS_MAP.contains(ip);
     }
     
     public synchronized void addIpToMap(String ip){
         IPS_MAP.add(ip);
     }
}
