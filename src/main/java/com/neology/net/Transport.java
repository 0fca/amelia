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
package com.neology.net;

import com.neology.exceptions.ClosedConnectionException;
import com.neology.exceptions.TransportException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @author dime,obsidiam
 */
public class Transport {
    public final static Charset ISO_8859_2 = Charset.forName("ISO-8859-2");
    public final static Charset UTF8 = Charset.forName("UTF-8");
    DataInputStream is;
    DataOutputStream os;
    InputStream origIs;
    OutputStream origOs;
    Socket s;
    private boolean WAS_CONNECTED = false;
    private BaudrateMeter baudrateMeter;
    private String IP;
    
    public Transport(Socket socket) throws IOException {
        this(socket.getInputStream(), socket.getOutputStream());
        this.s = socket;
        IP = socket.getInetAddress().getHostAddress();
    }

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
        WAS_CONNECTED = true;
    }

    public void close() throws IOException{
        release();
        s.close();
        IP = "";
    }
    
    public byte readByte() throws TransportException {
        try {
            if (baudrateMeter != null) baudrateMeter.count(1);
            return is.readByte();
        } catch (EOFException e) {
            throw new ClosedConnectionException(e);
        } catch (IOException e) {
            throw new TransportException("Cannot read byte", e);
        }

    }

    public void setBaudrateMeter(BaudrateMeter baudrateMeter) {
        this.baudrateMeter = baudrateMeter;
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
            throw new ClosedConnectionException(e);
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
            throw new ClosedConnectionException(e);
        } catch (IOException e) {
            throw new TransportException("Cannot read int32", e);
        }
    }

    public long readInt64() throws TransportException {
        try {
            if (baudrateMeter != null) baudrateMeter.count(8);
            return is.readLong();
        } catch (EOFException e) {
            throw new ClosedConnectionException(e);
        } catch (IOException e) {
            throw new TransportException("Cannot read int32", e);
        }
    }

    /**
     * Read string by it length.
     * Use this method only when sure no character accept ASCII will be read.
     * Use readBytes and character encoding conversion instead.
     *
     * @return String read
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
            throw new ClosedConnectionException(e);
        } catch (IOException e) {
            throw new TransportException("Cannot read " + length + " bytes array", e);
        }
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
            throw new ClosedConnectionException(e);
        } catch (IOException e) {
            throw new TransportException("Cannot skip " + length + " bytes", e);
        }
    }

    private void checkForOutputInit() throws TransportException {
        if (null == os) throw new TransportException("Uninitialized writer");
    }

    public Transport flush() throws TransportException {
        checkForOutputInit();
        try {
            os.flush();
        } catch (IOException e) {
            throw new TransportException("Cannot flush output stream", e);
        }
        return this;
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
         return s.isConnected();
    }
    
    public boolean wasConnected() {
         return WAS_CONNECTED;
    }
}
