/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.data;

import com.neology.net.Connection;
import com.neology.net.Connection;
import java.util.HashMap;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 *
 * @author obsidiam
 */
public final class ConnectionDataHandler{ 
     private volatile HashMap<String,String> DATA = new HashMap<>();
     private volatile HashSet<String> IPS_MAP = new HashSet<>(); 
     private static volatile ConnectionDataHandler INSTANCE = new ConnectionDataHandler();
     private final ObservableList<Connection> SOCKET_LIST = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
     private volatile HashMap<String,String> CONN_USER_DATA = new HashMap<>();
     private volatile boolean isFree = true;
     private volatile Connection udp;
     
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
     
     public synchronized ObservableList<Connection> getConnectionList(){
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
     
     public void setFree(boolean free){
         this.isFree = free;
     }
     
     public boolean isFree(){
         return isFree;
     }
     
     public void setUdpConnection(Connection udp){
         this.udp = udp;
     }
}
