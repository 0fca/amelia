/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.interfaces;

import com.neology.exceptions.ClosedConnectionException;
import com.neology.exceptions.TransportException;
import com.neology.net.states.Transport;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


/**
 *
 * @author Obsidiam
 */
public interface Connectable {
    public void openConnection(InputStream in, OutputStream out);
    public void closeConnection(Transport t) throws ClosedConnectionException;
    public void haltConnection(Transport t);
    public void sendPacket( byte[] buffer);
    public void establish(Transport t);
    public byte[] readPacket() throws TransportException;
    public byte[] readPacket(int len) throws TransportException;
}
