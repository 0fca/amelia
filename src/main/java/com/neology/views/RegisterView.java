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
package com.neology.views;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author obsidiam
 */
public class RegisterView extends AbstractView {

    private Parent parent;
    private FXMLLoader loader = new FXMLLoader();

    public RegisterView(String layout) {
        loader.setLocation(this.getClass().getResource(layout));
    }

    @Override
    public String getLayout() {
        return loader.getLocation().toString();
    }

    
    @Override
    public ViewFactory getFactory() {
        return ViewFactory.getInstance();
    }

    @Override
    public void loadView() {
        try {
            parent = loader.load();
            this.getChildren().add(parent);
            this.setStyle("-fx-background-color: #FFFFFF");
            AnchorPane.setTopAnchor(this, 0d);
            AnchorPane.setBottomAnchor(this, 0d);
            this.setPrefSize(((AnchorPane)parent).getPrefWidth(), ((AnchorPane)parent).getPrefHeight());
        } catch (IOException ex) {
            Logger.getLogger(RegisterView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public RegisterView getController() {
        return loader.getController();
    }
}
