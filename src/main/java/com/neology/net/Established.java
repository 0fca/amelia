/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.net;

import com.neology.exceptions.TransportException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obsidiam
 */
public class Established extends TransportState{
    @Override
    public void sendPacket(Transport t, byte[] buffer) {
        try {
            t.write(buffer);
        } catch (TransportException ex) {
            Logger.getLogger(Established.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public byte[] readPacket(Transport t) throws TransportException{
        System.out.println("Established readPacket()::"+t.getIp());
       return t.readBytes(8192);
    }
}
