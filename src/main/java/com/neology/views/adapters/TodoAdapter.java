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
package com.neology.views.adapters;

import com.neology.lastdays.TodoTicket;
import com.neology.views.Constants;
import com.neology.views.ContentAdapter;

/**
 *
 * @author obsidiam
 */
public class TodoAdapter implements ContentAdapter{
    private TodoTicket t;
    private String adaptedForm;
    
    @Override
    public String getContentType() {
        return TodoTicket.class.getCanonicalName();
    }

    @Override
    public void setContent(Object content) {
        if(content instanceof TodoTicket){
            t = (TodoTicket)content;
        }
    }

    @Override
    public Object getContent() {
        return t;
    }

    @Override
    
    public Object getAdaptedContent() {
        adapt();
        return adaptedForm;
    }

    @Override
    public void setCellHeight(int height) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getHeight() {
        return Constants.TODO_TICKET_CELL_HEIGHT;
    }
    
    private void adapt(){
        adaptedForm = t.getName()+"\n"+t.getState()+"\n"+t.getPriority().getName()+"\n";
    }
    
    public String getColor(){
        return t.getPriority().getColor();
    }
}
