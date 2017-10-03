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
package com.neology.controllers;

import com.neology.controllers.input.KeyboardStructure;
import com.neology.controllers.input.MouseStructure;
import com.neology.exceptions.BufferException;
import com.neology.net.UDPConnector;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author obsidiam
 */
public class RemoteDesktopSceneController implements Initializable {

    @FXML
    private Pane REMOTE_DESKTOP;
    
    private MouseStructure mouseStruct = MouseStructure.LOCATION;
    private KeyboardStructure keyboardStruct = KeyboardStructure.GET_SEQ;
    //private CopyOnWriteArrayList<MouseStructure> mousePoints = new CopyOnWriteArrayList<>();
    private  UDPConnector udp;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        udp = (UDPConnector)rb.getObject("UDPConnector");
        
        REMOTE_DESKTOP.setOnMouseClicked(listener ->{
            calculatePostition(listener.getSceneX(), listener.getSceneY());
            System.out.println("Clicked...");
        });

        
        REMOTE_DESKTOP.setOnKeyPressed( listener ->{
            try {
                commitCharacter(listener.getText());
            } catch (BufferException ex) {
                Logger.getLogger(RemoteDesktopSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }    

    private void calculatePostition(double x , double y) {
        double calcX = x/REMOTE_DESKTOP.getPrefWidth();
        double calcY = y/REMOTE_DESKTOP.getPrefHeight();
        
        Double calcXprec = BigDecimal.valueOf(calcX)
        .setScale(4, RoundingMode.HALF_UP)
        .doubleValue();
        
        Double calcYprec = BigDecimal.valueOf(calcY)
        .setScale(4, RoundingMode.HALF_UP)
        .doubleValue();
        
        mouseStruct.setLocation(calcXprec, calcYprec);
        udp.setMouseData(mouseStruct);
        System.out.println(calcXprec+" "+calcYprec);
        mouseStruct.hasChanged = true;
    }

    private void commitCharacter(String character) throws BufferException {
        if(!character.isEmpty()){
            keyboardStruct.addChar(character.charAt(0));
            //System.out.print(character);
            udp.setKeyboardData(keyboardStruct);
            keyboardStruct.hasChanged = true;
        }
    }
}
