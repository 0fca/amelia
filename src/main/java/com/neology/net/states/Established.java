/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net.states;

import com.neology.exceptions.TransportException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obsidiam
 */
public class Established extends TransportState{
    private Transport T;
    
    @Override
    public void establish(Transport t){
        this.T = t;
    }
    
    @Override
    public void sendPacket( byte[] buffer) {
        if(T.isTcp()){
            try {
                T.write(buffer);
            } catch (TransportException ex) {
                Logger.getLogger(Established.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            T.writeUdp(buffer);
        }
    }
    
    @Override
    public byte[] readPacket() throws TransportException{
       System.out.println("Established#readPacket()::"+T.getIp());
       return T.readBytes(8192);
    }
    
    @Override
    public byte[] readPacket(int len) throws TransportException{
       System.out.println("Established#readPacket(int)::"+T.getIp());
       if(T.isTcp()){
            return T.readBytes(len);
       }else{
            return T.readBytesUdp(len);
       }
    }
    
    @Override
    public void write(byte[] buffer) throws TransportException{
        System.out.println("Established#write(byte[])::"+T.getIp());
        T.write(buffer);
    }
    
    @Override
    public Transport getTransportInstance(){
        return T;
    }
}
