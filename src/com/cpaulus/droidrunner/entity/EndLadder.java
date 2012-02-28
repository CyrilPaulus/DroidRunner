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
public class EndLadder extends Block{
    
    private boolean active;
    
    public EndLadder(World w, Resources res) {
        super(w, res);
        setImage(res.getDrawable(R.drawable.ladder));
        type = Block.BlockType.ENDLADDER;
        active = false;
        
    }
    
    public void draw(Canvas c) {
        if(active)
            super.draw(c);
    }
    
    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isLadder() {
        return active;
    }

    @Override
    public boolean isRope() {
        return false;
    }

    @Override
    public boolean isAiSolid() {
        return false;
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
       
    
}
