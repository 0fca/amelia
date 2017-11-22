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

import com.neology.views.drawer.Drawer;

/**
 *
 * @author obsidiam
 */
public class ViewFactory {
    private static volatile ViewFactory viewFactory = new ViewFactory();
    
    private ViewFactory(){}
    
    public static synchronized ViewFactory getInstance(){
        return viewFactory;
    }
    
    @SuppressWarnings("unsafe")
    public AbstractView getConcreteView(String type){
        switch(type){
            case Constants.LOGIN:
                return new LoginView(type);
            case Constants.REGISTER:
                return new RegisterView(type);
            case Constants.DRAWER:
                return new Drawer(type);
            default:
                return null;
        }
    }
}
