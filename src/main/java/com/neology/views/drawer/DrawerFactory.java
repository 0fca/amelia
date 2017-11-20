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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author obsidiam
 */
public class DrawerFactory {
    private static final DrawerFactory df = new DrawerFactory();
    private static Drawer d;
    private DrawerFactory(){}
    
    
    public static DrawerFactory getInstance(){
        return df;
    }
    
    public Drawer getDrawer(String resource){
        try {
            d = new Drawer((FXMLLoader.load(this.getClass().getResource(resource))));
        } catch (IOException ex) {
            Logger.getLogger(DrawerFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return d;
    }
    
    public DrawerController getDrawerController(){
        return new FXMLLoader().getController();
    }
}
