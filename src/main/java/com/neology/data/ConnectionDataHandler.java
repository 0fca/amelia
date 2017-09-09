/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.data;

import com.neology.net.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author obsidiam
 */
public class ConnectionDataHandler { 
     private volatile HashMap<String,String> DATA = new HashMap<>();
     private volatile HashSet<String> IPS_MAP = new HashSet<>(); 
     private static volatile ConnectionDataHandler INSTANCE = new ConnectionDataHandler();
     private volatile List<Connection> SOCKET_LIST = Collections.synchronizedList(new ArrayList<>());
     private volatile HashMap<String,String> CONN_USER_DATA = new HashMap<>();
     
     private ConnectionDataHandler(){}
     
     public synchronized static ConnectionDataHandler getInstance(){
         return INSTANCE;
     }
     
     private volatile int PORT = 7999;
     
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
     
     public synchronized void removeFromIpMap(String ip){
         IPS_MAP.remove(ip);
     }
     
     public synchronized List<Connection> getConnectionList(){
         return SOCKET_LIST;
     } 
    
     public synchronized String findConnectionName(String userName){
          if(CONN_USER_DATA.containsKey(userName)){
              return CONN_USER_DATA.get(userName);
          }  
          return null;
     }
     
     public synchronized void putConnectionName(String userName, String ip){
            CONN_USER_DATA.put(userName, ip);
     }
     
     public synchronized void removeConnectionName(String userName, String ip){
         CONN_USER_DATA.remove(userName, ip);
     }
}
