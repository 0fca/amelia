/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neology.google;

import com.neology.controllers.SettingsFormsController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;


/**
 *
 * @author obsidiam
 */
public class GoogleService extends Service{

    @Override
    protected Task createTask() {
            try {

               Authorization.authorize();
                
            } catch (IOException ex) {
                Logger.getLogger(SettingsFormsController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        return null;
    }

}
