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

import com.neology.net.states.Established;
import com.neology.net.states.Transport;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author obsidiam
 */
public class UDPConnector {
    private volatile int port = 7998;
    private volatile Transport t;
    private volatile DatagramSocket serverSocket;
    private static boolean isPrepared = false;
    
    public UDPConnector(int port){
        this.port = port;
    }
    
    public void prepareConnection() throws SocketException{
        serverSocket = new DatagramSocket(port);
        t = new Transport();
        t.setDatagramSocket(serverSocket);
        Connection c = new Connection(t);
        Established e = new Established();
        c.changeState(e);
        isPrepared = true;
    }
    
    public void startThread(){
        if(isPrepared){
            UDPThread uth = new UDPThread(this);
            uth.start();
        }else{
            throw new IllegalStateException("UDPConnector was in not prepared state!");
        }
    }
    
    public DatagramPacket readDatagramPacket() throws IOException{
        byte[] buf = new byte[16384];
        DatagramPacket dt = new DatagramPacket(buf,buf.length);
        serverSocket.receive(dt);
        return dt;
    }
    
    public void sendDatagramPacket(DatagramPacket dt) throws IOException{
        serverSocket.send(dt);
    }
    
    private Transport getTransport(){
        return t;
    }
}
