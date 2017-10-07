/*
 * Copyright (C) 2017 lukas
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
package com.neology.parsing;

import com.google.gson.Gson;
import com.neology.data.Frame;
import com.neology.data.LoginData;
import com.neology.data.Session;
import com.neology.lastdays.TodoTicket;
import java.io.BufferedReader;
import java.util.ArrayList;

/**
 *
 * @author Obsidiam
 */
public class JSONController {
    private static volatile JSONController JSON;
    static BufferedReader br = null;
    private static final Gson GS = new Gson();
    private static LoginData ld;
    private static Frame frame;
    private static Session s = new Session();
    
    public static synchronized JSONController getInstance(){
        if(JSON == null){
            JSON = new JSONController();
        }
        return JSON;
    } 
    
    public boolean parseLogin(String json){
         ld = GS.fromJson(json, LoginData.class);
         s.setToken(ld.getToken());
         
        return (ld != null && ld.getSuccess());
    }
    
    public boolean registerUser(String json){
        ld = GS.fromJson(json, LoginData.class);
        return (ld != null && ld.getSuccess());
    }
    
    public Frame parseFrame(String json){
        return frame = GS.fromJson(json, Frame.class);
    }
    
    public Session getSession(){
        return s;
    }
}
