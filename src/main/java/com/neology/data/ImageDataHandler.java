/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.data;

import java.util.HashMap;
import javafx.scene.image.Image;

/**
 *
 * @author obsidiam
 */
public class ImageDataHandler {
    private volatile HashMap<String,Image> IMAGES = new HashMap<>();
    
    private volatile static ImageDataHandler IMG = new ImageDataHandler();
    
    private ImageDataHandler(){}
    
    public synchronized static ImageDataHandler getInstance(){
        return IMG;
    }

    
    public synchronized HashMap<String,Image> getImagesMap(){
        return IMAGES;
    }
}
