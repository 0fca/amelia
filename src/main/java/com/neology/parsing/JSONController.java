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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neology.environment.Local;
import com.neology.environment.LocalEnvironment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Obsidiam
 */
public class JSONController {
    private static volatile JSONController JSON;
    private static LocalEnvironment ENV = new LocalEnvironment(){};
    static BufferedReader br = null;
    
    public static synchronized JSONController getInstance(){
        if(JSON == null){
            JSON = new JSONController();
        }
        return JSON;
    } 
    
    public void writeJson(ArrayList<FileDataWrapper> sync) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(ENV.getLocalVar(Local.USER_HOME)+File.separator+"joanne"+File.separator+"sync_data.json"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(sync, writer);       
        writer.close();
    }
    
    public FileDataWrapper[] readArray() throws FileNotFoundException{
        br = new BufferedReader(new FileReader(ENV.getLocalVar(Local.USER_HOME)+File.separator+"joanne"+File.separator+"sync_data.json"));
        Gson gson = new Gson(); 
        FileDataWrapper[] wrapper = gson.fromJson(br, FileDataWrapper[].class);
        return wrapper;
    }
    
    public String readString(String field) throws FileNotFoundException{
        br = new BufferedReader(new FileReader(ENV.getLocalVar(Local.USER_HOME)+File.separator+"joanne"+File.separator+"sync_data.json"));
        JsonArray entries = (JsonArray) new JsonParser().parse(br);
        return ((JsonObject)entries.get(0)).get(field).toString();
    }
    
    public int readInt32(String field) throws FileNotFoundException{
        br = new BufferedReader(new FileReader(ENV.getLocalVar(Local.USER_HOME)+File.separator+"joanne"+File.separator+"sync_data.json"));
        JsonArray entries = (JsonArray) new JsonParser().parse(br);
        return ((JsonObject)entries.get(0)).get(field).getAsInt();
    }
    
   
    /**
     * Left for testing purposes, NOT an entry point!.
     * 
     */
    @Deprecated
    private static void main(String[] args) throws FileNotFoundException, IOException{
//        ArrayList<FileDataWrapper> sync = new ArrayList<>();
//        SyncDataWrapper s = new SyncDataWrapper();
//        s.setFileId(1);
//        s.setFileModifDate(1234566778L);
//        s.setFileName("File1");
//        SyncDataWrapper s2 = new SyncDataWrapper();
//        s2.setFileId(2);
//        s2.setFileModifDate(1234567890L);
//        s2.setFileName("File2");
//        sync.add(s);
//        sync.add(s2);
        //writeJson(sync,sync.size());
    }
}
