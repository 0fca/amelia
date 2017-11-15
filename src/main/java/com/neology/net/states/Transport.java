// Copyright (C) 2010 - 2014 GlavSoft LLC.
// All rights reserved.
//
// -----------------------------------------------------------------------
// This file is part of the TightVNC software.  Please visit our Web site:
//
//                       http://www.tightvnc.com/
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
// -----------------------------------------------------------------------
//
package com.neology.net.states;

import com.neology.exceptions.ClosedConnectionException;
import com.neology.exceptions.TransportException;
import com.neology.net.BaudrateMeter;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dime,obsidiam
 */
public class Transport {
    public final static Charset ISO_8859_2 = Charset.forName("ISO-8859-2");
    public final static Charset UTF8 = Charset.forName("UTF-8");
    private DataInputStream is;
    private DataOutputStream os;
    private InputStream origIs;
    private OutputStream origOs;
    private BaudrateMeter baudrateMeter;
    private String IP;
    private DatagramSocket dt;
    

    public Transport(InputStream is) {
        this(is, null);
    }

    public Transport(OutputStream os) {
        this(null, os);
    }

    public Transport(InputStream is, OutputStream os) {
        init(is, os);
    }

    void init(InputStream is, OutputStream os) {
        origIs = is;
        this.is = is != null ? new DataInputStream(is) : null;
        origOs = os;
        this.os = os != null ? new DataOutputStream(os) : null;
    }

    public Transport() {
        this(null, null);
    }

    void release() {
        origIs = is = null;
        origOs = os = null;
    }

    public void close() throws IOException{
        release();
    }
    
    public byte readByte() throws TransportException {
        try {
            if (baudrateMeter != null) baudrateMeter.count(1);
            return is.readByte();
        } catch (EOFException e) {
            throw new ClosedConnectionException("Cannot read byte",e);
        } catch (IOException e) {
            throw new TransportException("Cannot read byte", e);
        }

    }

    public void setBaudrateMeter(BaudrateMeter baudrateMeter) {
        this.baudrateMeter = baudrateMeter;
    }

    public BaudrateMeter getBaudrateMeter(){
        return this.baudrateMeter;
    }
    
    public int readUInt8() throws TransportException {
        return readByte() & 0x0ff;
    }

    public int readUInt16() throws TransportException {
        return readInt16() & 0x0ffff;
    }

    public short readInt16() throws TransportException {
        try {
            if (baudrateMeter != null) baudrateMeter.count(2);
            return is.readShort();
        } catch (EOFException e) {
            throw new ClosedConnectionException("Cannot read int16",e);
        } catch (IOException e) {
            throw new TransportException("Cannot read int16", e);
        }
    }

    public long readUInt32() throws TransportException {
        return readInt32() & 0xffffffffL;
    }

    public int readInt32() throws TransportException {
        try {
            if (baudrateMeter != null) baudrateMeter.count(4);
            return is.readInt();
        } catch (EOFException e) {
            throw new ClosedConnectionException("Cannot read int32",e);
        } catch (IOException e) {
            throw new TransportException("Cannot read int32", e);
        }
    }

    public long readInt64() throws TransportException {
        try {
            if (baudrateMeter != null) baudrateMeter.count(8);
            return is.readLong();
        } catch (EOFException e) {
            throw new ClosedConnectionException("Cannot read long",e);
        } catch (IOException e) {
            throw new TransportException("Cannot read long", e);
        }
    }

    /**
     * Read string by it length.
     * Use this method only when sure no character accept ASCII will be read.
     * Use readBytes and character encoding conversion instead.
     *
     * @param length the length of the string to be read
     * @return String - output string value
     * @throws com.neology.exceptions.TransportException
     */
    public String readString(int length) throws TransportException {
//        return new String(readBytes(length), ISO_8859_1);
        return stringWithBytesAndCharset(readBytes(length));
    }

    /**
     * Read 32-bit string length and then string themself by it length
     * Use this method only when sure no character accept ASCII will be read.
     * Use readBytes and character encoding conversion instead or {@link #readUtf8String} method
     * when utf-8 encoding needed.
     *
     * @return String read
     * @throws TransportException
     */
    public String readString() throws TransportException {
        // unset most significant (sign) bit 'cause InputStream#readFully reads
        // [int] length bytes from stream. Change when really need read string more
        // than 2147483647 bytes length
        int length = readInt32() & Integer.MAX_VALUE;
        return readString(length);
    }

    /**
     * Read 32-bit string length and then string themself by it length
     * Assume UTF-8 character encoding used
     *
     * @return String read
     * @throws TransportException
     */
    public String readUtf8String() throws TransportException {
        // unset most significant (sign) bit 'cause InputStream#readFully  reads
        // [int] length bytes from stream. Change when really need read string more
        // than 2147483647 bytes length
        int length = readInt32() & Integer.MAX_VALUE;
        return new String(readBytes(length), UTF8);
    }

    /**
     * Read @code{length} byte array
     * Create byte array with length of @code{length}, read @code{length} bytes and return the array
     * @param length
     * @return byte array which contains the data read
     * @throws TransportException
     */
    public byte[] readBytes(int length) throws TransportException {
        byte b[] = new byte[length];
        return readBytes(b, 0, length);
    }

    public byte[] readBytes(byte[] b, int offset, int length) throws TransportException {
        try {
            is.readFully(b, offset, length);
            if (baudrateMeter != null) {
                baudrateMeter.count(length);
            }
            return b;
        } catch (EOFException e) {
            throw new ClosedConnectionException("Cannot read " + length + " bytes array",e);
        } catch (IOException e) {
            throw new TransportException("Cannot read " + length + " bytes array", e);
        }
    }

    public byte[] readBytesUdp(int len){
        byte[] b = new byte[len];
        DatagramPacket p = new DatagramPacket(b, len);
        if(dt != null){
           try {
               getDatagramSocket().receive(p);
           } catch (IOException ex) {
               Logger.getLogger(Established.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
        return p.getData();
    }
    
    public void skip(int length) throws TransportException {
        try {
            int rest = length;
            do {
                rest -= is.skipBytes(rest);
            } while (rest > 0);
            if (baudrateMeter != null) {
                baudrateMeter.count(length);
            }
        } catch (EOFException e) {
            throw new ClosedConnectionException("Cannot skip " + length + " bytes",e);
        } catch (IOException e) {
            throw new TransportException("Cannot skip " + length + " bytes", e);
        }
    }

    private void checkForOutputInit() throws TransportException {
        if (null == os) throw new TransportException("Uninitialized writer");
    }

    public boolean flush() throws TransportException {
        checkForOutputInit();
        try {
            os.flush();
        } catch (IOException e) {
            throw new TransportException("Cannot flush output stream", e);
        }
        return true;
    }

    public Transport writeByte(int b) throws TransportException {
        return write((byte) (b & 0xff));
    }

    public Transport write(byte b) throws TransportException {
        checkForOutputInit();
        try {
            os.writeByte(b);
        } catch (IOException e) {
            throw new TransportException("Cannot write byte", e);
        }
        return this;
    }

    public Transport writeInt16(int sh) throws TransportException {
        return write((short) (sh & 0xffff));
    }

    public Transport write(short sh) throws TransportException {
        checkForOutputInit();
        try {
            os.writeShort(sh);
        } catch (IOException e) {
            throw new TransportException("Cannot write short", e);
        }
        return this;
    }

    public Transport writeInt32(int i) throws TransportException {
        return write(i);
    }

    public Transport writeUInt32(long i) throws TransportException {
        return write((int) i & 0xffffffff);
    }

    public Transport writeInt64(long i) throws TransportException {
        checkForOutputInit();
        try {
            os.writeLong(i);
        } catch (IOException e) {
            throw new TransportException("Cannot write long", e);
        }
        return this;
    }

    public Transport write(int i) throws TransportException {
        checkForOutputInit();
        try {
            os.writeInt(i);
        } catch (IOException e) {
            throw new TransportException("Cannot write int", e);
        }
        return this;
    }

    public Transport write(byte[] b) throws TransportException {
        return write(b, 0, b.length);
    }

    public Transport write(byte[] b, int length) throws TransportException {
        return write(b, 0, length);
    }

    public Transport write(byte[] b, int offset, int length) throws TransportException {
        checkForOutputInit();
        try {
            os.write(b, offset, length <= b.length ? length : b.length);
        } catch (IOException e) {
            throw new TransportException("Cannot write " + length + " bytes", e);
        }
        return this;
    }

    public void setOutputStreamTo(OutputStream os) {
        this.os = new DataOutputStream(os);
    }

    public Transport zero(int count) throws TransportException {
        while (count-- > 0) {
            writeByte(0);
        }
        return this;
    }

  private String stringWithBytesAndCharset(byte[] bytes) {
    String result;
    try {
      result = new String(bytes, ISO_8859_2);
    } catch (NoSuchMethodError error) {
      try {
        result = new String(bytes, ISO_8859_2.name());
      } catch (UnsupportedEncodingException e) {
        result = null;
      }
    }
    return result;
  }
  
    public String getIp(){
        return IP;
    }
    
    public void setIp(String ip){
        this.IP = ip;
    }
    
    public boolean isConnected() {
        return dt == null ? (origIs != null && origOs != null) : dt.isConnected(); 
    }
    

    public void setDatagramSocket(DatagramSocket dt){
        this.dt = dt;
    }
    
    public DatagramSocket getDatagramSocket(){
        return dt;
    }
    boolean isTcp(){
        return dt == null;
    }

    void writeUdp(byte[] buffer) {
        if(dt != null){
            DatagramPacket p = new DatagramPacket(buffer, buffer.length);

            try {
                getDatagramSocket().send(p);
            } catch (IOException ex) {
                Logger.getLogger(Established.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
