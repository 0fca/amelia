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
package com.neology.controllers.cells;

import com.neology.lastdays.TodoTicket;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 *
 * @author obsidiam
 */
 public class DefaultInfoViewListCell<T> extends ListCell<T> {
        @Override 
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if(item instanceof TodoTicket){
                    TodoTicket ticket = (TodoTicket)item;
                    setText("Name: "+ticket.getName()+"\n"+"State: "+ticket.getState()+"\n"+"Importance: "+ticket.getPriority().getName());
                    setPrefHeight(this.getFont().getSize()*5);
                    backgroundProperty().bind(Bindings.when(this.visibleProperty())
                    .then(new Background(new BackgroundFill(Color.valueOf(ticket.getPriority().getColor()), new CornerRadii(2d), new Insets(10,0,0,0))))
                    .otherwise(new Background(new BackgroundFill(Color.DARKCYAN, new CornerRadii(2d), Insets.EMPTY))));
                }else{
                    setText(item.toString().replace(",","\n"));
                }
            }
        }
    }
