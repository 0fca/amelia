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

import com.neology.controllers.FXMLController.ViewUpdater;
import com.neology.exceptions.AccessDeniedException;

/**
 *
 * @author obsidiam
 */
final public class AccessorImpl implements Accessable{
    private ViewUpdater instance;
    private boolean hasAccess = false;
    private Thread th;
    
    AccessorImpl(ViewUpdater v){
        this.instance = v;
    }
    
    @Override
    public void sendNotificationSignal(SignalType s) {
        if(hasAccess){
            instance.signal(s);
        }else{
            throw new AccessDeniedException("Access is denied for "+th.getName());
        }
    }


    @Override
    public boolean checkViewUpdaterAccess(Thread t) throws AccessDeniedException{
        try{
         instance.checkAccess();
         hasAccess = true;
        }finally{
            if(hasAccess){
                return true;
            }
        }
        this.th = t;
        return false;
    }

    @Override
    public void commitSignalData(String msg) {
        instance.commitSignalData(msg);
    }
}
