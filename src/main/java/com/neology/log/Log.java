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
package com.neology.log;

import com.neology.environment.Local;
import com.neology.environment.LocalEnvironment;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author obsidiam
 */
public final class Log {
    private Log(){}
    
    private static void cleanSystemStdout() throws IOException{
        if(LocalEnvironment.getLocalVar(Local.OS).contains("Windows")){
            Runtime.getRuntime().exec("cls");
        }else{
            Runtime.getRuntime().exec("clear");
        }
    }
    
    public static void log(String tag, String msg){
        System.out.println(tag+" : "+msg);
    }
    
    public static void log(PrintStream ps, String tag, String msg){
        ps.println(tag+" : "+msg);
    }
    
    public static void log(String format, String tag, String msg){
        System.out.printf(format, tag);
    }
    
    public static void signAsLast(){
        try {
            cleanSystemStdout();
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
