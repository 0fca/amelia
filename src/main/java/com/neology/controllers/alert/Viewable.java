/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.controllers.alert;

import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author Obsidiam
 */
public interface Viewable {
    public void viewInfo(String name, String header, String content, AlertType type);
    public void viewError(String text);
    public void viewCustom(String title, String headerText, String contentText);
    public void viewNotification(String title, String content, Integer hideAfter, Pos p);
   
        
}
