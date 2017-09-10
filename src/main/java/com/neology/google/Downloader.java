/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.google;

import com.google.api.services.drive.Drive;
import com.neology.environment.Local;
import static com.neology.google.Authorization.getDriveService;
import com.neology.environment.LocalEnvironment;
import com.neology.parsing.JSONController;
import com.neology.parsing.FileDataWrapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Obsidiam
 */
public class Downloader {
    private static Drive driveService = null;
    private static LocalEnvironment ENV = new LocalEnvironment(){};
    private static  FileOutputStream fs;
    private static Downloader DF;
    private ArrayList<FileDataWrapper> DATA_STORE = new ArrayList<>();
    
    public static synchronized Downloader getInstance(){
        if(DF == null){
            DF = new Downloader();
        }
        return DF;
    }

    public void setConfigData(ArrayList<FileDataWrapper> sync){
        this.DATA_STORE = sync;
    }
    
    public void downloadFiles() throws IOException {
        driveService = getDriveService();
        ArrayList<String> ids = Authorization.getFilesIds();
        ArrayList<String> names = Authorization.getFilesList();
        ArrayList<FileDataWrapper> records = new ArrayList<>();
        Date d = new Date();
        long l_date = d.getTime();
        
        DATA_STORE.forEach(wrapper ->{
            if(names.contains(wrapper.fileName())){
                names.remove(wrapper.fileName());
            }
        });
       
            FileDataWrapper wrapper = new FileDataWrapper();
            wrapper.setFileModifDate(l_date);

            ids.forEach(fileId ->{
                wrapper.setFileId(Integer.parseInt(fileId));
                File out_dir = new File(ENV.getLocalVar(Local.USER_HOME)+File.separator+"amelia-server"+File.separator+"backup"+File.separator+names.get(ids.indexOf(fileId)));
                try {
                    fs = new FileOutputStream(out_dir);
                    driveService.files().get(fileId)
                    .executeMediaAndDownloadTo(fs);
                    wrapper.setFileName(out_dir.getName());
                } catch (IOException ex) {
                    Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
                }
                records.add(wrapper);
            });

        JSONController.getInstance().writeJson(records);
        
    }
    
    public void stop(){
        try {
            if(fs != null){
                fs.flush();
                fs.close();
                System.out.println("FS closed.");
            }
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
