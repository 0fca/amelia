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
package com.neology.controllers.input;

import com.neology.exceptions.BufferException;

/**
 *
 * @author obsidiam
 */
public enum KeyboardStructure {
    KEYBAORD_LAYOUT,
    GET_CHAR(' '),
    GET_SEQ(new byte[1024]);
    
    private char tmpCharacter = 0;
    private byte[] seq = new byte[1024];
    private int load = 0;
    public boolean hasChanged = false;
    
    KeyboardStructure(byte[] s){
        this.seq = s;
    }
    
    KeyboardStructure(char c){
        this.tmpCharacter = c;
    }
    
    KeyboardStructure(){
    }
    
    
    public void addChar(char c) throws BufferException{
        this.tmpCharacter = c;
        if(load < seq.length){
            seq[load] = (byte)c;
            load++;
        }else{
            throw new BufferException("Over the buffer's limit: "+seq.length);
        }
    }
    
    public char getChar(){
        return tmpCharacter;
    }
    
    public byte[] getSequence(){
        return seq;
    }
    
    public void setSequence(byte[] seq){
        this.seq = seq;
    }
    
    public void clearSequence(){
        this.seq = new byte[1024];
        load = 0;
    }
    
    public boolean loadState(){
        return load != 0;
    }
    
    
}
