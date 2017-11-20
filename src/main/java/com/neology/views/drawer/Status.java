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
package com.neology.views.drawer;

/**
 *
 * @author obsidiam
 */
public class Status {
    private boolean showIndicator = false;
    private boolean unlockAnyButtons = false;
    
    private String[] buttonNames;
    
    public boolean shouldShowIndicator(){
        return showIndicator;
    }
    
    public void setShouldShowIndicator(boolean show){
        this.showIndicator = show;
    }
    
    public void setUnlockAnyButtons(boolean unlock){
        this.unlockAnyButtons = unlock;
    }
    
    public boolean unlockAnyButtons(){
        return unlockAnyButtons;
    }
    
    public void setButtonNames(String[] names){
        this.buttonNames = names;
    }
    
    public String[] getButtonNames(){
        return buttonNames;
    }
}
