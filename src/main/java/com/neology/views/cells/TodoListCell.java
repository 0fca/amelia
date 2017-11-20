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
package com.neology.views.cells;

import com.neology.views.adapters.TodoAdapter;
import com.neology.lastdays.TodoTicket;
import com.neology.views.Constants;
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
public class TodoListCell<T> extends ListCell<T>{
    TodoAdapter ca;
    public TodoListCell(TodoAdapter a){
        this.ca = a;
    }
    
    @Override
    public void updateItem(T item, boolean empty){
        if(empty){
        }else{
            ca.setContent(item);
            setText(ca.getAdaptedContent().toString());
            backgroundProperty().bind(Bindings.when(this.visibleProperty())
            .then(new Background(new BackgroundFill(Color.valueOf(ca.getColor()), new CornerRadii(2d), Insets.EMPTY)))
            .otherwise(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(2d), Insets.EMPTY))));
            setPrefHeight(this.getFont().getSize() * Constants.TODO_TICKET_CELL_HEIGHT);
        }
    }
}
