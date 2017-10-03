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

import com.neology.controllers.input.KeyboardStructure;
import com.neology.controllers.input.MouseStructure;
import com.neology.data.ConnectionDataHandler;
import com.neology.net.states.Established;
import com.neology.net.states.Transport;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author obsidiam
 */
public class UDPConnector {
    private volatile int port = 7998;
    private volatile String ip = "localhost";
    private volatile Transport t;
    private volatile DatagramSocket serverSocket;
    private volatile MouseStructure mouseStruct = MouseStructure.LOCATION;
    private volatile KeyboardStructure keyboardStruct = KeyboardStructure.GET_SEQ;
    
    private static boolean isPrepared = false;
    
    public UDPConnector(String ip,int port){
        this.port = port;
        this.ip = ip;
    }
    
    public void prepareConnection() throws SocketException{
        serverSocket = new DatagramSocket(port);
        t = new Transport();
        t.setIp(ip);
        t.setDatagramSocket(serverSocket);
        Connection c = new Connection(t);
        ConnectionDataHandler.getInstance().setUdpConnection(c);
        Established e = new Established();
        c.changeState(e);
        isPrepared = true;
    }
    
    public void startThread(){
        if(isPrepared){
            UDPThread uth = new UDPThread(this);
            uth.start();
        }else{
            throw new IllegalStateException("UDPConnector wasn't in prepared state!");
        }
    }
    
    DatagramPacket readDatagramPacket(byte[] buf) throws IOException{
        DatagramPacket dt = new DatagramPacket(buf,buf.length);
        serverSocket.receive(dt);
        return dt;
    }
    
    void sendDatagramPacket(DatagramPacket dt) throws IOException{
        if(dt.getData() != null){
            serverSocket.send(dt);
            //System.out.println("Sending...");
        }
    }
    
    private Transport getTransport(){
        return t;
    }
    
    public synchronized void setMouseData(MouseStructure m){
        this.mouseStruct = m;
    }
    
    public synchronized void setKeyboardData(KeyboardStructure k){
        this.keyboardStruct = k;
    }
    
    MouseStructure getMouseStrucutre(){
        return mouseStruct;
    }
    
    KeyboardStructure getKeyboardStructure(){
        return keyboardStruct;
    } 
    
    public InetAddress getAddress() throws UnknownHostException{
        return InetAddress.getByName(t.getIp());
    }
    
   public boolean wasWritten(){
       return keyboardStruct.loadState();
   }
}
