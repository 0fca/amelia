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

import com.neology.net.LoginExecutor;
import com.neology.RestClient;
import com.neology.controllers.alert.AlertMethod;
import com.neology.views.Constants;
import com.neology.views.LoginView;
import com.neology.views.RegisterView;
import com.neology.views.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author obsidiam
 */
public class RegisterViewController implements Initializable {

    @FXML
    private TextField LOGIN_REG,EMAIL;
    @FXML
    private PasswordField PASS_REG;
    @FXML
    private Button REGISTER_BUTTON;
    
    private RestClient rest;
    private AlertController ac = new AlertController();
    private static RegisterView rv;
    
    static RegisterView getViewInstance(){
        if(rv != null){
            return rv;
        }else{
            rv = (RegisterView)ViewFactory.getInstance().getConcreteView(Constants.REGISTER);
            rv.loadView();
            return rv;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        rest = new RestClient();
         REGISTER_BUTTON.setOnAction(event ->{
            if(LOGIN_REG.getText() != null && PASS_REG.getText() != null && EMAIL.getText() != null){
                if(LoginExecutor.validLoginDataFormat(PASS_REG.getText(), EMAIL.getText(), PASS_REG.getText())){
                    rest.init();
                    //progressIndicator.setVisible(true);
                    if(rest.registerUser(LOGIN_REG.getText(), PASS_REG.getText(),EMAIL.getText())){
                        ac.prepareViewable(new Object[]{"Register","Registering Last Days' account","Registering to Last Days successful!",Alert.AlertType.INFORMATION});
                        ac.viewAlert(AlertMethod.INFO);
                        PASS_REG.setText(null);
                        LOGIN_REG.setText(null);
                        EMAIL.setText(null);
                        //progressIndicator.setVisible(false);
                        animateViewMove();
                    }else{
                        ac.prepareViewable(new Object[]{"Error. Couldn't register account "+LOGIN_REG.getText()});
                        ac.viewAlert(AlertMethod.ERROR);
                    }
                }
            }else{
                 ac.prepareViewable(new Object[]{"Error. You mustn't leave any field empty."});
                 ac.viewAlert(AlertMethod.ERROR);
            }
        });
    }    
    
    static void animateViewMove() {
        TranslateTransition openNav = new TranslateTransition(new Duration(350), rv);
        openNav.setToX(rv.getWidth());
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), rv);
       
        if(rv.getTranslateX() < rv.getWidth()){
            rv.requestFocus();
            openNav.play();
        }else{
            closeNav.setToX(-(rv.getWidth()));
            closeNav.play();
        }
    } 
}
