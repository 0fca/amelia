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
package com.neology.data;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author obsidiam
 */
public class UDPConnectorResources extends ResourceBundle{

    ConcurrentHashMap<String, Object>  hs = new ConcurrentHashMap<>();
            
    
    public void putObject(String key,Object o){
        hs.put(key, o);
    }
    
    @Override
    protected Object handleGetObject(String key) {
        return hs.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return hs.keys();
    }

   
    
}
