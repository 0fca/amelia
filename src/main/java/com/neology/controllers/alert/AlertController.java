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
package com.neology.controllers.alert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 *
 * @author obsidiam
 */
public class AlertController{
    private ViewableImpl vi = new ViewableImpl();
    private Object[] args = new Object[]{""};
    private Class<?>[] types;
  
    
    public void prepareViewable(Object[] args){
        this.args = args;
        bindTypes();
    }
    
    public void prepareViewable(Service s, Object[] args){
        vi = new ViewableImpl(s);
        this.args = args;
        bindTypes();
    }

    public Object viewAlert(AlertMethod m){
        try {
            Method method = ViewableImpl.class.getDeclaredMethod(m.getValue(m.toString()), types);
            return method.invoke(vi, args);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(AlertController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    
    private void bindTypes() {
        types = new Class<?>[args.length];
       
        
        for(int i = 0; i < args.length; i++){
            if(types.length < i){
                break;
            }
            if(types[i] == null){
                types[i] = args[i].getClass();
            }else if(!args[i].getClass().getCanonicalName().equals(types[i].getClass().getCanonicalName())){
                types[i] = args[i].getClass();
            }
            
        }
    }
    
    private class ViewableImpl implements Viewable{
        Alert a = new Alert(AlertType.NONE);
        Service t = null;
        
        ViewableImpl(){
            a.getDialogPane().getStylesheets().add("/styles/Styles.css");
        }
        
        ViewableImpl(Service s){
            this.t = s;
            a.getDialogPane().getStylesheets().add("/styles/Styles.css");
        }   

        @Override
        public void viewInfo(String name, String header, String content, Alert.AlertType type) {
            a.setAlertType(type);
            a.setTitle(name);
            a.setHeaderText(header);
            a.setContentText(content);
            a.showAndWait();
        }

        @Override
        public void viewError(String text) {
            a.setAlertType(AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("An error occured.");
            a.setContentText(text);
            a.showAndWait();
        }

        @Override
        public void viewCustom(String title, String headerText, String content) {
            if(t != null){
                Dialog<ArrayList<String>> dialog = new Dialog<>();
                dialog.setWidth(300);
                dialog.setTitle(title);
                dialog.setHeaderText(headerText);
                
                ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, cancelButtonType);

                VBox vbox = new VBox();
                vbox.setSpacing(10);
                vbox.setAlignment(Pos.CENTER);
                vbox.setPrefWidth(dialog.getWidth());
                vbox.getChildren().add(new Label(content));
                ProgressBar p = new ProgressBar();
                p.setPrefWidth(300);

                vbox.getChildren().add(p);

                // Enable/Disable login button depending on whether a username was entered.
                Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
                loginButton.setDisable(true);

                Node cancelButton = dialog.getDialogPane().lookupButton(cancelButtonType);
                cancelButton.setOnMouseClicked(event ->{
                    t.cancel();
                });

                t.setOnCancelled(event ->{
                    t.cancel();
                });

                t.setOnSucceeded(success_evt ->{
                    System.out.println("Succeded.");
                    loginButton.setDisable(false);
                    cancelButton.setDisable(true);
                });
                dialog.getDialogPane().setContent(vbox);
                dialog.getDialogPane().getStylesheets().add("/styles/Styles.css");
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == loginButtonType) {

                    }
                    return null;
                });

                Optional<ArrayList<String>> result = dialog.showAndWait();

                result.ifPresent(usernamePassword -> {


                });
            }
        }

        @Override
        public void viewNotification(String title, String content, Integer hideAfter, Pos p) {
             Notifications notificationBuilder = Notifications.create()
                    .title(title)
                    .text(content)
                    .graphic(null)
                    .hideAfter(Duration.seconds(hideAfter))
                    .position(p);
            notificationBuilder.show();
        }
       
   }
    
}
