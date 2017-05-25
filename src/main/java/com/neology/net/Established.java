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
    public boolean wasConnected(Transport t) {
        return t.wasConnected();
    }

    @Override
    public boolean isConnected(Transport t) {
        return t.isConnected();
    }

    @Override
    public void sendPacket(Transport t, byte[] buffer) {
        try {
            t.write(buffer);
        } catch (TransportException ex) {
            Logger.getLogger(Established.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public byte[] readPacket(Transport t, byte[] buffer) throws TransportException{
        t.readBytes(buffer, 0, 8192);
        return buffer;
    }
}
