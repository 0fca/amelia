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
package com.neology.views.drawer;

import com.neology.controllers.DrawerController;
import com.neology.log.Log;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author obsidiam
 */
public class Drawer extends AnchorPane{
    private Pane contentPane;
    
    public Drawer(AnchorPane p){
        this.contentPane = p;    
        this.setPrefWidth(((Pane)contentPane.getChildren().get(0)).getPrefWidth());
        AnchorPane.setTopAnchor(contentPane, 0d);
        AnchorPane.setBottomAnchor(contentPane, 0d);
        AnchorPane.setRightAnchor(contentPane, 0d);
        this.getChildren().add(p);
        Log.log("Drawer",String.valueOf(contentPane.getPrefHeight()));
    }
    
    public Pane getDrawerContent(){
        return this.contentPane;
    }
    
    public DrawerController getController(){
        return DrawerFactory.getInstance().getDrawerController();
    }
}
