/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.parsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Obsidiam
 */
public class XMLManager {
    private boolean checkIfExists(File path){ 
        return path.exists();
    }
    
    public void deleteFile(File path) throws IOException{
        if(checkIfExists(path)){
            Files.delete(path.toPath());
        }
    }
    
    public void renameFile(File path,String new_name) throws IOException{
        List<String> content = Files.readAllLines(path.toPath());
        File out = new File(new_name);
        BufferedWriter fw = new BufferedWriter(new FileWriter(out));
        content.forEach(line ->{
            try {
                fw.append(line);
            } catch (IOException ex) {
                Logger.getLogger(XMLManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        fw.flush();
        fw.close();
    }
}
