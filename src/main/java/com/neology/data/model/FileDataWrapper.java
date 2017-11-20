package com.neology.data.model;

/**
 *
 * @author obsidiam
 */
public class FileDataWrapper{
    private int fileId;
    private long fileMDate;
    private String fileName;

    public void setFileId(int id){
        this.fileId = id;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    } 
    
    public void setFileModifDate(long fileMDate){
        this.fileMDate = fileMDate;
    }
    
    public int getFileId(){
        return this.fileId;
    }
    
    public long getFileMDate(){
        return this.fileMDate;
    }
    
    public String fileName(){
        return this.fileName;
    }
}
