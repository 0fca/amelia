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

import com.neology.environment.Local;
import com.neology.environment.LocalEnvironment;
import com.neology.views.Constants;
import com.neology.views.ViewFactory;
import com.neology.views.drawer.Drawer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author obsidiam
 */
public class DrawerController implements Initializable {
    @FXML
    private Pane DRAWER;
    @FXML
    private Label USER_IMG, arrowBack, usernameLbl;
    @FXML
    private Button showLoginView,showRegisterView,showAboutForm;
    
    private static Drawer drawer;
   
    static Drawer getViewInstance(){
        if(drawer != null){
            return drawer;
        }else{
            drawer = (Drawer)ViewFactory.getInstance().getConcreteView(Constants.DRAWER);
            drawer.loadView();
            return drawer;
        }
    }

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       
       showLoginView.setOnAction(event ->{
            LoginViewController.animateViewMove();
       });
       
       showAboutForm.setOnAction(event ->{
           AboutFormController about;
            try {
                about = new AboutFormController(FXMLLoader.load(getClass().getResource(Constants.ABOUT_FORM)));
                about.showAbout();
            } catch (IOException ex) {
                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
       });
       
       showRegisterView.setOnAction(evne ->{
           RegisterViewController.animateViewMove();
       });
       
       arrowBack.setOnMouseClicked(event ->{
           animateDrawerMove();
       });
       
       arrowBack.setGraphic(new ImageView(this.getClass().getResource("/images/back_arrow.png").toExternalForm()));
    }
    
    static void animateDrawerMove() {
        TranslateTransition openNav = new TranslateTransition(new Duration(350), drawer);
        openNav.setToX(drawer.getWidth());
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), drawer);
        
        if(drawer.getTranslateX() < drawer.getWidth()){
            openNav.play();
            
        }else{
            closeNav.setToX(-(drawer.getWidth()));
            closeNav.play();
        }
    }
    
    void setProfileImage(String name) {
        ImageView userProfile = null;
        if(name != null){
            File file = new File(LocalEnvironment.getLocalVar(Local.TMP)+File.separator+name.toLowerCase()+".png");
            Image i;
            if(!file.exists()){
               i = new Image(this.getClass().getResource(Constants.USER_IMG).toString(), 32,32, true,true);
            }else{
               i = new Image("file:///"+file.getAbsolutePath(), 48,48, true,true);
            }
            userProfile = new ImageView(i);
        }
        
        USER_IMG.setGraphic(userProfile);
    } 
    
    void setUserName(String name){
        usernameLbl.setText(name);
    }
}
