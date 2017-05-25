/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.interfaces;

import com.neology.net.Transport;
import java.io.InputStream;
import java.io.OutputStream;


/**
 *
 * @author Obsidiam
 */
public interface Connectable {
    public boolean isConnected(Transport t);
    public boolean wasConnected(Transport t);
    public void openConnection(InputStream in, OutputStream out);
    public void closeConnection(Transport t);
    public void haltConnection(Transport t);
    public void sendPacket(Transport t, byte[] buffer);
}
