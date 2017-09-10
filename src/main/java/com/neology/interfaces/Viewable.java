/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.interfaces;

import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author Obsidiam
 */
public interface Viewable {
    public void viewAlert(String name, String header, String content, AlertType type);
    public void viewError(String text);
    public void viewCustom();
}
