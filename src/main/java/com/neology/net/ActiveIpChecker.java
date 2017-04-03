/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import abstracts.LocalEnvironment;
import enums.Local;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Obsidiam
 * @deprecated 
 */

public class ActiveIpChecker extends Thread implements Runnable{
   Transporter TRANS = Transporter.getInstance(); 
   private Thread inside = null;
   private CopyOnWriteArrayList<String> out = new CopyOnWriteArrayList<>();
   private boolean IS_CONNECTED = false;
   
   private NetController net = new NetController();
   
   LocalEnvironment local = new LocalEnvironment(){
       @Override
       public String preparePath(String path) {
           throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       }
   };
   
   @Override
   public void start(){
       if(inside == null){
           inside = new Thread(this,"AIPC");
           inside.setDaemon(true);
           inside.start();
       }
   }
   
   @Override
   public void run(){
       try {
           this.out = net.prepareActiveIpsList(updateIpInformation());
           //System.out.println(out.size());
           this.TRANS.prepareConnection(out);
           System.out.println("Ips checked!");
           IS_CONNECTED = true;
           
       } catch (Exception ex) {
           Logger.getLogger(ActiveIpChecker.class.getName()).log(Level.SEVERE, null, ex);
       }
   }

   public boolean getConnectionState(){
       return IS_CONNECTED;
   }
   
   private CopyOnWriteArrayList<String> updateIpInformation() throws NullPointerException, UnknownHostException, SocketException, Exception {
        
        CopyOnWriteArrayList<String> temp = new CopyOnWriteArrayList<>();
        List<String> list;
   
           list = Arrays.asList(net.getIpPool(local.getLocalVar(Local.SUBNET)));
            list.forEach(ip ->{
                try {
                   temp.add(ip);
                } catch (Exception ex) {
                    Logger.getLogger(ActiveIpChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        return temp;
   }
}

