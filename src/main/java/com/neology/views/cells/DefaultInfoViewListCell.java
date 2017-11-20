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

import com.neology.views.Constants;
import com.neology.views.adapters.ContentAdapter;
import javafx.scene.control.ListCell;

/**
 *
 * @author obsidiam
 */
 public class DefaultInfoViewListCell<T> extends ListCell<T>  {
     private ContentAdapter ca;
    @Override 
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            ca.setContent(item);
            setText(ca.getAdaptedContent().toString());

            if(ca.getContentType().contains("PlainTextAdapter")){
                setPrefHeight(this.getFont().getSize() * Constants.PLAIN_TEXT_CELL_HEIGHT);
            }
            
        }
    }
    
    public void setContentAdapter(ContentAdapter ca){
        this.ca = ca;
    }
}
