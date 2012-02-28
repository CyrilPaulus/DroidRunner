/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.entity;

import android.content.res.Resources;
import android.graphics.Canvas;
import com.cpaulus.droidrunner.R;

/**
 *
 * @author cyril
 */
public class Goal extends Entity{
    private boolean active;
    
    public Goal(World w, Resources res) {
        super(w);        
        setImage(res.getDrawable(R.drawable.goal));
        active = true;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public void draw(Canvas c) {
        if(active)
            super.draw(c);
    }
}
