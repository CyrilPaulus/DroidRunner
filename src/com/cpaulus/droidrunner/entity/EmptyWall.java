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
public class EmptyWall extends Block{

    public EmptyWall(World w, Resources res) {
        super(w, res);
        setImage(res.getDrawable(R.drawable.empty));
        type = Block.BlockType.EMPTY;
    }
    
    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isLadder() {
        return false;
    }

    @Override
    public boolean isRope() {
        return false;
    }
        
    @Override
    public void draw(Canvas c) {
        
    }

    @Override
    public boolean isAiSolid() {
        return false;
    }
    
}
