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

import com.neology.views.ContentAdapter;

/**
 *
 * @author obsidiam
 */
public class PlainTextAdapter implements ContentAdapter{

    String content;
    private int height;
    
    @Override
    public String getContentType() {
        return "String";
    }

    @Override
    public void setContent(Object content) {
        if(content instanceof String){
            this.content = String.valueOf(content);
        }
    }

    @Override
    public Object getContent() {
        return content;
    }
    
    private void adapt(){
        content = content.replace(",","\n");
    }

    @Override
    public Object getAdaptedContent() {
        adapt();
        return content;
    }

    @Override
    public void setCellHeight(int height) {
        this.height = height;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
