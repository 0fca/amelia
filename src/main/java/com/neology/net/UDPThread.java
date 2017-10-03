/*
 * Copyright (C) 2017 zsel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.neology.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author obsidiam
 */
public class UDPThread extends Thread implements Runnable {
    private Thread TH;
    private UDPConnector uc;
    private DatagramPacket dataPacket;
    
    public UDPThread(UDPConnector uc){
        this.uc = uc;
    }
    
    @Override
    public void start(){
        if(TH == null){
            TH = new Thread(this,"UDPThread");
            TH.start();
        }
    }
    
    @Override
    public void run(){
        while(TH != null){
            if(!TH.isInterrupted()){
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket dtg = uc.readDatagramPacket(buffer);
                    String s = new String(dtg.getData());
                    //System.out.println(s.trim());
                    if(s.trim().contains("hndshk")){
                        prepareDatagramPacket(uc.getAddress(), dtg.getPort());
                        System.out.println(uc.getAddress());
                        uc.sendDatagramPacket(dataPacket);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(UDPThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void processInputData(byte[] buffer){
        System.out.print(new String(buffer));
    }
    
    private byte[] processOutputData(){
        byte[] buffer = new byte[1024];
        int index = 0;
         
        if(uc.getMouseStrucutre().hasChanged){
            byte[] x = String.valueOf(uc.getMouseStrucutre().getX()).getBytes();
            byte[] y = String.valueOf(uc.getMouseStrucutre().getY()).getBytes();
  
            for(byte b : x){
                buffer[index] = b;
                index++;
            }
            buffer[index + 1] = -1;

            index = x.length + 2;

            for(byte b : y){
                buffer[index] = b;
                index++;
            }
            uc.getMouseStrucutre().hasChanged = false;
        }else{
            buffer[0] = -2;
        }
        
        byte[] keyboardInput = uc.getKeyboardStructure().getSequence();
        if(!uc.getKeyboardStructure().hasChanged){
            buffer[index + 1] = -127;
            return buffer;
        }else{
            buffer[index + 1] = -128;

            index += 2;

            for(byte b : keyboardInput){
                if(((int)b) > 0){
                    buffer[index] = b;
                    //System.out.println(b);
                }
                index++;
            }
            uc.getKeyboardStructure().clearSequence();
            uc.getKeyboardStructure().hasChanged = false;
            
        }
        //TODO:add other metadata etc.; accessible length: 1022
        
        
        return buffer;
    }
    
    private void prepareDatagramPacket(InetAddress address, int port){
        byte[] buffer = processOutputData();
        dataPacket = new DatagramPacket(buffer,buffer.length, address, port);
        dataPacket.setData(buffer);
    }
    
    @Override
    public void interrupt(){
        if(TH != null){
            TH.interrupt();
            TH = null;
        }
    }
    
}
